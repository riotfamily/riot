package org.riotfamily.revolt.support;


/**
 * Exception thrown by the DialectResolver if no suitable dialect was found.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DatabaseNotSupportedException extends RuntimeException {

	public DatabaseNotSupportedException(String name, int major, int minor) {
		super(name + " " + major + "." + minor);
	}
}
