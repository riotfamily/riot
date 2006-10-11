package org.riotfamily.pages.page.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletMappingHelper;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.PageMap;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class PageInterceptor extends HandlerInterceptorAdapter {

	private ViewModeResolver viewModeResolver;

	private PageMap pageMap;
	
	private ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper();
	
	public PageInterceptor(PageMap pageMap, ViewModeResolver viewModeResolver) {
		this.pageMap = pageMap;
		this.viewModeResolver = viewModeResolver;
	}

	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		PageUtils.exposePageMap(request, pageMap);
		Page page = PageUtils.getPage(request);
		if (page == null) {
			String path = servletMappingHelper.getLookupPathForRequest(request);
			page = pageMap.getPageOrAncestor(path);
			Assert.notNull(page, "No page found");
			PageUtils.exposePage(request, page);
		}
		return page.isPublished() || (viewModeResolver != null 
					&& viewModeResolver.isPreviewMode(request));
	}

}
