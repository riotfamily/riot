package org.riotfamily.pages.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.editor.ComponentEditor;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class InstantPublishModeInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		Page page = PageUtils.getPage(request);
		if (page != null) {
			request.setAttribute(ComponentEditor.INSTANT_PUBLISH_ATTRIBUTE,
					Boolean.valueOf(page.isNew()));
		}
		return true;
	}

}
