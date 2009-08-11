package org.riotfamily.forms;




/**
 * Interface to be implemented by elements that need to execute a client side
 * script in order to be functional. 
 */
public interface DHTMLElement extends Element {

	/**
	 * Returns a JavaScript that is evaluated in order to initialize the 
	 * element, or <code>null</code> if no initialization is needed.
	 */
	public String getInitScript();

}
