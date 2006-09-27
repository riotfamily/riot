package org.riotfamily.pages.component.context;

public class RequestContextExpiredException extends Exception {

	private static final String MESSAGE = "Request context has expired";
	
	public RequestContextExpiredException() {
		super(MESSAGE);
	}

}
