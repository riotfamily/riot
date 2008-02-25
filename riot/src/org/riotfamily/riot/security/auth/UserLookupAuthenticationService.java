package org.riotfamily.riot.security.auth;

/**
 * AuthenticationService that supports lookups by id without credentials.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface UserLookupAuthenticationService extends AuthenticationService {

	public RiotUser getUserById(String userId);

}
