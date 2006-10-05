package org.riotfamily.pages.page.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.page.Page;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class PublishedPageInterceptor extends HandlerInterceptorAdapter {

	private ViewModeResolver viewModeResolver;
	
	public PublishedPageInterceptor(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {

		Page page = PageUtils.getPage(request);
		return page.isPublished() || (viewModeResolver != null 
					&& viewModeResolver.isPreviewMode(request));
	}

}
