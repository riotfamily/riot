package org.riotfamily.core.security.session;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.filter.FilterPlugin;
import org.riotfamily.core.security.auth.RiotUser;

/**
 * Servlet filter that binds the authenticated user (if present) to the
 * current thread. 
 * 
 * @see LoginManager#getUser(HttpServletRequest)
 * @see SecurityContext#bindUserToCurrentThread(RiotUser)
 */
public final class AccessControlFilterPlugin extends FilterPlugin {

	public AccessControlFilterPlugin() {
		setOrder(0);
	}

	public void doFilter(HttpServletRequest request,
		HttpServletResponse response, FilterChain filterChain)
		throws IOException, ServletException {
		
		try {
			LoginManager loginManager = LoginManager.getInstance(getServletContext());
			RiotUser user = loginManager.getUser(request);
			SecurityContext.bindUserToCurrentThread(user);
			filterChain.doFilter(request, response);
		}
		finally {
			SecurityContext.resetUser();
		}
	}
}
