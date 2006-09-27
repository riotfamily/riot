package org.riotfamily.forms.factory;

import org.springframework.core.NestedRuntimeException;

/**
 * Exception to be thrown by element factories to indicate configuration errors.
 * Note: This is an unchecked (runtime) exception as these kinds of errors are
 * always considered fatal. 
 */
public class FormDefinitionException extends NestedRuntimeException {

	
	public FormDefinitionException(String msg) {
		super(msg);
	}

	public FormDefinitionException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
