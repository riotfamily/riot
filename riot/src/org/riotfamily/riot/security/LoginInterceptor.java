package org.riotfamily.riot.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.security.ui.LoginFormController;

public class LoginInterceptor extends AccessControlInterceptor {
	
	private LoginFormController loginFormController;
	
	public void setLoginFormController(LoginFormController loginFormController) {
		this.loginFormController = loginFormController;
	}

	/**
	 * Returns <code>true</code> if a principal is set, otherwise 
	 * <code>false</code> is returned and a redirect to the login form is sent.
	 */
	protected boolean isAuthorized(HttpServletRequest request,
			HttpServletResponse response, String principal) throws Exception {
		
		if (principal != null) {
			return true;
		}
		else {
			String loginFormUrl = loginFormController.getLoginFormUrl(request);
			response.sendRedirect(response.encodeRedirectURL(loginFormUrl));
			return false;
		}
	}
	
}
