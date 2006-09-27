package org.riotfamily.riot.security.support;

import org.riotfamily.riot.security.AuthenticationService;

/**
 * AuthenticationService that uses a fixed username/password combination.
 * This class is indended for development purposes only.
 */
public class StaticAuthenticationService implements AuthenticationService {

	public static final String SUBJECT = "root";
	
	public static final String DEFAULT_USERNAME = "admin";
	
	public static final String DEFAULT_PASSWORD = "admin";
	
	
	private String username = DEFAULT_USERNAME;
	
	private String password = DEFAULT_PASSWORD;
	
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String authenticate(String username, String password) {
		if (this.username.equals(username) && this.password.equals(password)) {
			return SUBJECT;
		}
		return null;
	}

}
