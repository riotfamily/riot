package org.riotfamily.riot.security;


public interface AuthenticationService {

	/**
	 * @param username The username
	 * @param password The password (plaintext)
	 * 
	 * @return The principal or <code>null</code> if the user could not be
	 * authenticated
	 */
	public String authenticate(String username, String password);
}
