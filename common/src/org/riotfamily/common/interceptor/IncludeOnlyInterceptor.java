package org.riotfamily.common.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.RiotLog;
import org.springframework.web.util.WebUtils;

/**
 * HandlerInterceptor that sends a 404 error if the request is not an 
 * include request, i.e. was not dispatched by a {@link RequestDispatcher}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class IncludeOnlyInterceptor extends PathMatchingInterceptor {

	private RiotLog log = RiotLog.get(IncludeOnlyInterceptor.class);
	
	protected boolean doPreHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		if (!WebUtils.isIncludeRequest(request)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			log.warn("Direct access prevented by IncludeOnlyInterceptor");
			return false;
		}
		return true;
	}

}
