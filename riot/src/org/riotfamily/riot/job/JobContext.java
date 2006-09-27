package org.riotfamily.riot.job;

import org.riotfamily.riot.job.support.JobTask;


public class JobContext {

	private JobTask task;
	
	public JobContext(JobTask task) {
		this.task = task;
	}

	/**
	 * Returns the objectId.
	 */
	public String getObjectId() {
		return task.getDetail().getObjectId();
	}

	/**
	 * Notifies the DAO that a step has been completed.
	 * @throws JobInterruptedException if the job has been interrupted 
	 */
	public void stepCompleted() throws JobInterruptedException {
		task.stepCompleted();	
	}
			
	/**
	 * Logs an info message.
	 */
	public void logInfo(String message) {
		task.logInfo(message);
	}
	
	/**
	 * Logs an error message.
	 */
	public void logError(String message) {
		task.logError(message);
	}
		
}
