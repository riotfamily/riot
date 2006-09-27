package org.riotfamily.riot.security.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.riot.security.PrincipalBinder;

public class HttpSessionPrincipalBinder implements PrincipalBinder {

	public static final String DEFAULT_SESSION_KEY = "riotSubject";
	
	private String sessionKey = DEFAULT_SESSION_KEY;
	
	
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	protected String getSessionKey() {
		return sessionKey;
	}

	public String getPrincipal(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (String) session.getAttribute(sessionKey);
	}

	public void bindPrincipalToRequest(String principal, 
			HttpServletRequest request) {
		
		request.getSession().setAttribute(sessionKey, principal);
	}

	public void unbind(HttpServletRequest request, 
			HttpServletResponse response) {
		
		request.getSession().removeAttribute(sessionKey);		
	}

}
