package org.riotfamily.core.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.core.security.AccessController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Sets cache control headers to prevent client side caching if a Riot user 
 * is logged in. This is especially useful if the user modifies a page via
 * AJAX, leaves the page and hits the back button.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class NoCacheHeaderInterceptor extends HandlerInterceptorAdapter {

	public void postHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		if (!response.isCommitted() && modelAndView != null 
				&& AccessController.isAuthenticatedUser()) {
			
			ServletUtils.setNoCacheHeaders(response);
		}
	}
}
