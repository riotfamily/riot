package org.riotfamily.riot.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.OncePerRequestInterceptor;

/**
 * HandlerInterceptor that binds the authenticated principal (if present) to the
 * current thread. 
 * 
 * @see AccessController
 */
public class AccessControlInterceptor extends OncePerRequestInterceptor {

	public boolean preHandleOnce(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		String principal = AccessController.getPrincipal(request);
		AccessController.bindPrincipalToCurrentThread(principal);
		return isAuthorized(request, response, principal);
	}

	protected boolean isAuthorized(HttpServletRequest request,
			HttpServletResponse response, String principal) throws Exception {
		
		return true;
	}
	
	public final void afterLastCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		AccessController.resetPrincipal();
	}

}
