package org.riotfamily.core.security.auth;



/**
 * Interface to be returned by an {@link AuthenticationService}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface RiotUser {

	public String getUserId();

	public String getName();
	
	public String getEmail();
	
}
