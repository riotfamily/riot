package org.riotfamily.riot.job;



/**
 * Context that is passed to a {@link Job} upon execution.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface JobContext {

	/**
	 * Returns the objectId.
	 */
	public String getObjectId();

	/**
	 * Notifies the DAO that a step has been completed.
	 * @throws JobInterruptedException if the job has been interrupted 
	 */
	public void stepCompleted() throws JobInterruptedException;
			
	/**
	 * Logs an info message.
	 */
	public void logInfo(String message);
	
	/**
	 * Logs an error message.
	 */
	public void logError(String message);
	
	/**
	 * Changes the job's description.
	 */
	public void updateDescription(String description);
	
	/**
	 * Changes the number of total steps.
	 */
	public void updateStepsTotal(int stepsTotal, boolean resetStepsCompleted);
			
}
