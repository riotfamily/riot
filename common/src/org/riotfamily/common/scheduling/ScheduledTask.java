package org.riotfamily.common.scheduling;

/**
 * Interface for scheduled background tasks.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public interface ScheduledTask {

	/**
	 * Returns the name of the triggers on which the task should be executed.
	 */
	public String[] getTriggerNames();

	/**
	 * Executes the task. The method is invoked by the Quartz scheduler when
	 * one of the configured triggers fires.
	 */
	public void execute() throws Exception;

}
