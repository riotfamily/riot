package org.riotfamily.revolt;



/**
 * @author Felix Gnass [fgnass at neteye dot de]
 *
 */
public class EvolutionException extends RuntimeException {

	public EvolutionException(String message) {
		super(message);
	}
	
	public EvolutionException(Throwable cause) {
		super(cause);
	}
	
	public EvolutionException(String message, Throwable cause) {
		super(message, cause);
	}

}
