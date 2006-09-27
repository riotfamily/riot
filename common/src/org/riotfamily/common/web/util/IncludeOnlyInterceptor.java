package org.riotfamily.common.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

public class IncludeOnlyInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		if (!WebUtils.isIncludeRequest(request)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, 
					"This URL can be requested through a " +
					"RequestDispatcher only.");
			
			return false;
		}
		return true;
	}

}
