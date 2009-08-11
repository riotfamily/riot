package org.riotfamily.core.security.auth;



public interface AuthenticationService {

	/**
	 * @param username The username
	 * @param password The password (plaintext)
	 * 
	 * @return The RiotUser or <code>null</code> if the user could not be
	 * authenticated
	 */
	public RiotUser authenticate(String username, String password);
}
