package org.riotfamily.riot.job;

/**
 * Exception that is thrown to signal a {@link Job} that an interruption has 
 * been requested.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class JobInterruptedException extends RuntimeException {

	public JobInterruptedException() {
	}

}
