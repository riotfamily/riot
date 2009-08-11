package org.riotfamily.common.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * HandlerInterceptor that exposes data stored in the {@link FlashScope} as
 * request attributes.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class FlashScopeInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		FlashScope.expose(request);
		return true;
	}
}
