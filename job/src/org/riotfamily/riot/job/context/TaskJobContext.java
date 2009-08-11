package org.riotfamily.riot.job.context;

import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobInterruptedException;
import org.riotfamily.riot.job.support.JobTask;

public class TaskJobContext implements JobContext {
	
	private JobTask task;
	
	public TaskJobContext(JobTask task) {
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
	
	public void updateDescription(String description) {
		task.updateDescription(description);
	}
	
	public void updateStepsTotal(int stepsTotal, boolean resetStepsCompleted) {
		task.updateStepsTotal(stepsTotal);
		if (resetStepsCompleted) {
			task.getDetail().setStepsCompleted(0);
		}
	}

}
