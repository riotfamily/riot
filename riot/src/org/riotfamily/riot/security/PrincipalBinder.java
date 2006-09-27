package org.riotfamily.riot.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see org.riotfamily.riot.security.support.HttpSessionPrincipalBinder
 */
public interface PrincipalBinder {

	public String getPrincipal(HttpServletRequest request);
	
	public void bindPrincipalToRequest(String principal, 
			HttpServletRequest request);
	
	public void unbind(HttpServletRequest request, 
			HttpServletResponse response);
}
