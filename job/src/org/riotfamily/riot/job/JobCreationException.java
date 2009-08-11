package org.riotfamily.riot.job;

import org.springframework.core.NestedRuntimeException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class JobCreationException extends NestedRuntimeException {

	public JobCreationException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public JobCreationException(String msg) {
		super(msg);
	}
	
}
