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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.scheduling;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.StatefulJob;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 * @deprecated
 */
public class TransactionalJobDetailFactoryBean implements 
		FactoryBean, BeanNameAware, BeanFactoryAware, InitializingBean {
	
	private PlatformTransactionManager transactionManager;
	
	private TransactionCallback callback;
	
	private String name;

	private String group = Scheduler.DEFAULT_GROUP;

	private boolean concurrent = true;
	
	private String[] jobListenerNames;
	
	private String beanName;
	
	private BeanFactory beanFactory;
	
	private JobDetail jobDetail;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setCallback(TransactionCallback callback) {
		this.callback = callback;
	}

	/**
	 * Set the name of the job.
	 * <p>Default is the bean name of this FactoryBean.
	 * @see org.quartz.JobDetail#setName
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the group of the job.
	 * <p>Default is the default group of the Scheduler.
	 * @see org.quartz.JobDetail#setGroup
	 * @see org.quartz.Scheduler#DEFAULT_GROUP
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	
	/**
	 * Specify whether or not multiple jobs should be run in a concurrent
	 * fashion. The behavior when one does not want concurrent jobs to be
	 * executed is realized through adding the {@link StatefulJob} interface.
	 * More information on stateful versus stateless jobs can be found
	 * <a href="http://www.opensymphony.com/quartz/tutorial.html#jobsMore">here</a>.
	 * <p>The default setting is to run jobs concurrently.
	 */
	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}
	
	/**
	 * Set a list of JobListener names for this job, referring to
	 * non-global JobListeners registered with the Scheduler.
	 * <p>A JobListener name always refers to the name returned
	 * by the JobListener implementation.
	 * @see SchedulerFactoryBean#setJobListeners
	 * @see org.quartz.JobListener#getName
	 */
	public void setJobListenerNames(String[] names) {
		this.jobListenerNames = names;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	public void afterPropertiesSet() {
		if (transactionManager == null) {
			transactionManager = (PlatformTransactionManager) 
					beanFactory.getBean("transactionManager", 
					PlatformTransactionManager.class);
		}

		Assert.notNull(callback, "A TransactionCallback must be set.");
		
		// Use specific name if given, else fall back to bean name.
		String name = (this.name != null ? this.name : this.beanName);
		
		// Consider the concurrent flag to choose between stateful and stateless job.
		Class<? extends Job> jobClass = (this.concurrent ? TransactionalJob.class : StatefulTransactionalJob.class);
		
		this.jobDetail = new JobDetail(name, this.group, jobClass);
		this.jobDetail.getJobDataMap().put("transactionManager", transactionManager);
		this.jobDetail.getJobDataMap().put("callback", callback);
		this.jobDetail.setVolatility(true);
		this.jobDetail.setDurability(true);

		// Register job listener names.
		if (this.jobListenerNames != null) {
			for (int i = 0; i < this.jobListenerNames.length; i++) {
				this.jobDetail.addJobListener(this.jobListenerNames[i]);
			}
		}
	}
	
	public Object getObject() {
		return this.jobDetail;
	}

	public Class<?> getObjectType() {
		return JobDetail.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
	public static class TransactionalJob extends QuartzJobBean {

		private PlatformTransactionManager transactionManager;
		
		private TransactionCallback callback;
		
		public void setTransactionManager(PlatformTransactionManager transactionManager) {
			this.transactionManager = transactionManager;
		}

		public void setCallback(TransactionCallback callback) {
			this.callback = callback;
		}

		protected void executeInternal(JobExecutionContext context) 
				throws JobExecutionException {

			new TransactionTemplate(transactionManager).execute(callback);
			
		}
		
	}
	
	/**
	 * Extension of the TransactionalJob, implementing the StatefulJob interface.
	 * Quartz checks whether or not jobs are stateful and if so,
	 * won't let jobs interfere with each other.
	 */
	public static class StatefulTransactionalJob extends TransactionalJob implements StatefulJob {
		// No implementation, just an addition of the tag interface StatefulJob
		// in order to allow stateful method invoking jobs.
	}
}
