package org.riotfamily.riot.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.security.session.SessionData;
import org.riotfamily.riot.security.session.SessionDataStore;

public class LoginManager {

	
	private AuthenticationService authService;
	
	private PrincipalBinder principalBinder;
	
	private SessionDataStore sessionDataStore; 
	
	
	public LoginManager(AuthenticationService authService, 
			PrincipalBinder binder) {
		
		this.authService = authService;
		this.principalBinder = binder;
	}

	public void setSessionDataStore(SessionDataStore sessionDataStore) {
		this.sessionDataStore = sessionDataStore;
	}

	public boolean login(HttpServletRequest request, String username, 
			String password) {
		
		String principal = authService.authenticate(username, password);
		if (principal != null) {
			principalBinder.bindPrincipalToRequest(principal, request);
			SessionData sessionData = null;
			if (sessionDataStore != null) {
				sessionData = sessionDataStore.loadSessionData(principal);
			}
			if (sessionData == null) {
				sessionData = new SessionData();
			}
			
			sessionData.setUsername(username);
			sessionData.setPrincipal(principal);
			sessionData.newSession(request, sessionDataStore);
			
			return true;
		}
		return false;
	}
	
	public void logout(HttpServletRequest request, 
			HttpServletResponse response) {
		
		principalBinder.unbind(request, response);
		request.getSession().invalidate();
	}
}
