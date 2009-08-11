package org.riotfamily.riot.job;

/**
 * Interface to define background jobs.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface Job {

	/**
	 * Creates a JobDescription and optionally performs setup tasks.
	 */
	public JobDescription setup(String objectId) throws JobCreationException;
	
	/**
	 * Returns whether the job can be executed concurrently with different 
	 * objectIds.
	 */
	public boolean isConcurrent();
	
	/**
	 * Returns whether the job can be executed more than once with the same
	 * objectId.  
	 */
	public boolean isRepeatable();
	
	/**
	 * Performs the actual work. The given context can be used to log messages
	 * or to provide progress information.
	 */
	public void execute(JobContext context) throws Exception;

	/**
	 * Invoked when a job is canceled or completed.
	 */
	public void tearDown(String objectId);

}
