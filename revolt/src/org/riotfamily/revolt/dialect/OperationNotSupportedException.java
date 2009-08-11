package org.riotfamily.revolt.dialect;

import org.riotfamily.revolt.EvolutionException;

/**
 * Exception that is thrown by a Dialect if an operation is not supported 
 * by the database (or not implemented by the Dialect).
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class OperationNotSupportedException extends EvolutionException {

	public OperationNotSupportedException(String message) {
		super(message);
	}

}
