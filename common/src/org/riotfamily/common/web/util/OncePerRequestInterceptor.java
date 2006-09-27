package org.riotfamily.common.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class OncePerRequestInterceptor extends PathMatchingInterceptor {

	private String counterAttribute = getClass().getName() + ".interceptions";

	protected final boolean doPreHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		int interceptions = 0;
		Integer counter = (Integer) request.getAttribute(counterAttribute);
		if (counter != null) {
			interceptions = counter.intValue();
		}
		interceptions++;
		request.setAttribute(counterAttribute, new Integer(interceptions));

		if (interceptions == 1) {
			return preHandleOnce(request, response, handler);
		}
		
		return true;
	}

	protected boolean preHandleOnce(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		return true;
	}
	
	public final void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception)
			throws Exception {
		
		Integer counter = (Integer) request.getAttribute(counterAttribute);
		if (counter != null) {
			int interceptions = counter.intValue() - 1;
			request.setAttribute(counterAttribute, new Integer(interceptions));
			if (interceptions == 0) {
				afterLastCompletion(request, response, handler, exception);
			}
		}
	}

	protected void afterLastCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception)
			throws Exception {
	}

}
