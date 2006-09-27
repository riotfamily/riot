package org.riotfamily.riot.job;

/**
 * Interface to define background jobs.
 */
public interface Job {

	/**
	 * Creates a JobDescription and optionally performs setup tasks.
	 */
	public JobDescription setup(String objectId);
	
	/**
	 * Returns whether the job can be executed concurrently with different 
	 * objectIds.
	 */
	public boolean isConcurrent();
	
	/**
	 * Performs the actual work. The given context can be used to log messages
	 * or to provide progress information.
	 */
	public void execute(JobContext context);

	/**
	 * Invoked when a job is canceled or completed.
	 */
	public void tearDown(String objectId);

}
