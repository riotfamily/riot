/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.scheduling;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Factory that creates a Quartz scheduler and registers all 
 * {@link ScheduledTask} instances found in the ApplicationContext.
 * <p>
 * The factory looks up all Trigger beans with a 
 * {@link Trigger#getName() trigger name} referenced by one the tasks.
 * You don't have to provide any {@link JobDetail JobDetails} as the
 * factory will create them automatically.
 * </p>  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class ScheduledTaskSchedulerFactoryBean extends SchedulerFactoryBean 
		implements BeanNameAware {

	private RiotLog log = RiotLog.get(ScheduledTaskSchedulerFactoryBean.class);
	
	private List<ScheduledTask> tasks;
	
	private List<Trigger> triggers = Generics.newArrayList();
	
	private Scheduler scheduler;
		
	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		super.setApplicationContext(ctx);
		tasks = SpringUtils.orderedBeans(ctx, ScheduledTask.class);
		Set<String> triggerNames = Generics.newHashSet();
		for (ScheduledTask task : tasks) {
			for (String name : task.getTriggerNames()) {
				triggerNames.add(name);
			}
		}
		for (Trigger trigger : SpringUtils.listBeansOfType(ctx, Trigger.class)) {
			if (triggerNames.contains(trigger.getName())) {
				triggers.add(trigger);
			}
		}
	}

	@Override
	public void setTriggers(Trigger[] triggers) {
		throw new BeanCreationException(
				"This Scheduler does not support manually registered Triggers");
	}
	
	public void setBeanName(String name) {
		setSchedulerName(name);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		setJobFactory(new ScheduledTaskJobFactory());
		super.afterPropertiesSet();
	}

	@Override
	public Object getObject() {
		scheduler = (Scheduler) super.getObject();
		for (Trigger trigger : triggers) {
			try {
				JobDetailBean job = new JobDetailBean();
				job.setName(trigger.getName());
				job.setJobClass(ScheduledTaskQueueJob.class);
				scheduler.scheduleJob(job, trigger);
			} 
			catch (SchedulerException e) {
				log.error("Error adding Trigger", e);
				throw new BeanCreationException("Error adding Trigger", e);
			}
		}
		return scheduler;
	}
		
	private class ScheduledTaskJobFactory implements JobFactory {

		private Map<String, ScheduledTaskQueueJob> jobs = Generics.newHashMap();
		
		public ScheduledTaskJobFactory() {
			for (ScheduledTask task : tasks) {
				for (String triggerName : task.getTriggerNames()) {
					ScheduledTaskQueueJob job = jobs.get(triggerName);
					if (job == null) {
						job = new ScheduledTaskQueueJob(triggerName);
						jobs.put(triggerName, job);
					}
					job.addTask(task);
				}
			}
		}
		
		public Job newJob(TriggerFiredBundle tfb) throws SchedulerException {
			String triggerName = tfb.getTrigger().getName();
			return jobs.get(triggerName);
		}
		
	}
	
	public static class ScheduledTaskQueueJob implements StatefulJob {
		
		private RiotLog log = RiotLog.get(ScheduledTaskQueueJob.class);
		
		private List<ScheduledTask> tasks = Generics.newArrayList();
		
		private String triggerName;
		
		public ScheduledTaskQueueJob(String triggerName) {
			this.triggerName = triggerName;
		}

		public void addTask(ScheduledTask task) {
			log.info(String.format("Adding %s to %s trigger", task.getClass(), triggerName));
			tasks.add(task);
		}
		
		public void execute(JobExecutionContext context)
				throws JobExecutionException {
			
			for (ScheduledTask task : tasks) {
				try {
					task.execute();
				}
				catch (Exception e) {
					log.error("Task execution failed.", e);
				}
			}
		}
	}
}
