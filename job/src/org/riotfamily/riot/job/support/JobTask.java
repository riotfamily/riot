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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job.support;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobInterruptedException;
import org.riotfamily.riot.job.context.TaskJobContext;
import org.riotfamily.riot.job.dao.JobDao;
import org.riotfamily.riot.job.model.JobDetail;
import org.riotfamily.riot.job.model.JobLogEntry;
import org.riotfamily.riot.job.ui.JobUIUpdater;
import org.springframework.dao.DataAccessException;


public class JobTask implements Runnable {
	
	private Log log = LogFactory.getLog(JobTask.class);
	
	private Job job;

	private JobDetail detail;
	
	private JobDao dao;
	
	private JobUIUpdater uiUpdater;

	private TaskList taskList;
	
	private Log jobLog;
	
	private Thread thread;
	
	private boolean interrupted;
	

	public JobTask(Job job, JobDetail detail, JobDao dao, 
			JobUIUpdater uiUpdater, TaskList taskList) {
		
		this.job = job;
		this.detail = detail;
		this.dao = dao;
		this.uiUpdater = uiUpdater;
		this.taskList = taskList;
		
		jobLog = LogFactory.getLog(job.getClass());
	}

	public void interrupt() {
		if (interrupted) {
			return;
		}
		interrupted = true;
		if (thread != null) {
			thread.interrupt();
			try {
				synchronized (thread) {
					thread.wait(10000);	
				}
			}
			catch (InterruptedException e) {
			}
			if (detail.getState() < JobDetail.INTERRUPTED) {
				log.warn("Job did not interrupt its work within 10 seconds.");
			}
		}
	}
	
	public boolean isInterrupted() {
		Thread.yield();
		return interrupted;
	}

	public void run() {
		if (interrupted) {
			return;
		}
		thread = Thread.currentThread();
		try {
			jobStarted();
			job.execute(new TaskJobContext(this));
		}
		catch (JobInterruptedException e) {
		}
		catch (Exception e) {
			interrupted = true;
			logError(e.getMessage());
			log.error("Job aborted due to exception", e);
		}
		jobStoped(!interrupted);
		synchronized (thread) {
			thread.notifyAll();	
		}
	}
	
	public JobDetail getDetail() {
		return this.detail;
	}
	
	/**
	 * Notifies the DAO that a step has been completed.
	 * @throws JobInterruptedException if the job has been interrupted 
	 */
	public void stepCompleted() throws JobInterruptedException {
		if (detail.getStepsTotal() > 0) {
			detail.stepCompleted();
			dao.updateJobDetail(detail);
			uiUpdater.updateJob(detail);
		}
		if (isInterrupted()) {
			throw new JobInterruptedException();
		}	
	}
			
	public void updateExecutionTime() {
		detail.updateExecutionTime();
		dao.updateJobDetail(detail);	
	}
	
	public void updateDescription(String description) {
		detail.setDescription(description);
		dao.updateJobDetail(detail);
		uiUpdater.updateJob(detail);
	}
	
	public void updateStepsTotal(int stepsTotal) {
		detail.setStepsTotal(stepsTotal);
		dao.updateJobDetail(detail);
		uiUpdater.updateJob(detail);
	}
	
	/**
	 * Logs an info message.
	 */
	public void logInfo(String message) {
		jobLog.info(message);
		log(message, JobLogEntry.INFO);
	}
	
	/**
	 * Logs an error message.
	 */
	public void logError(String message) {
		jobLog.error(message);
		log(message, JobLogEntry.ERROR);
	}
	
	/**
	 * Logs a message.
	 */
	protected void log(String message, int priority) {
		JobLogEntry entry = new JobLogEntry(detail, priority, message);
		try {
			dao.log(entry);
		}
		catch (DataAccessException e) {
			log.error("Can't save log entry: " + entry.getMessage());
		}
		uiUpdater.log(entry);
	}
	
	private void jobStarted() {
		taskList.addTask(this);
		if (detail.getStartDate() == null) {
			detail.setStartDate(new Date());
			logInfo("Job started");
		}
		else {
			logInfo("Job resumed");
		}
		detail.setState(JobDetail.STARTED);
		dao.updateJobDetail(detail);
		uiUpdater.updateJob(detail);
	}
	
	private void jobStoped(boolean completed) {
		taskList.removeTask(this);
		if (completed) {
			job.tearDown(detail.getObjectId());
			detail.setState(JobDetail.COMPLETED);
			detail.setEndDate(new Date());
			logInfo("Job completed");
		}
		else {
			detail.setState(JobDetail.INTERRUPTED);
		}
		dao.updateJobDetail(detail);
		uiUpdater.updateJob(detail);
	}
	
}