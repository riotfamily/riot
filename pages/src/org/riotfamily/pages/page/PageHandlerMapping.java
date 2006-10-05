package org.riotfamily.pages.page;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletMappingHelper;
import org.riotfamily.pages.component.preview.DefaultViewModeResolver;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.page.support.PageUtils;
import org.riotfamily.pages.page.support.PublishedPageInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

public class PageHandlerMapping implements HandlerMapping, Ordered, 
		InitializingBean {

	private static Log log = LogFactory.getLog(PageHandlerMapping.class);
	
	private ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper();
	
	private PageMap pageMap;
	
	private HandlerInterceptor[] interceptors;
	
	private int order = Integer.MAX_VALUE;
	
	private String pageAttribute;
		
	private ViewModeResolver viewModeResolver = new DefaultViewModeResolver();
	
	public PageHandlerMapping(PageMap map) {
		this.pageMap = map;
	}

	public void setPageAttribute(String pageAttribute) {
		this.pageAttribute = pageAttribute;
	}

	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	protected PageMap getPageMap() {
		return this.pageMap;
	}

	public void setInterceptors(HandlerInterceptor[] interceptors) {
		this.interceptors = interceptors;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void afterPropertiesSet() throws Exception {
		HandlerInterceptor publishedPageInterceptor = 
				new PublishedPageInterceptor(viewModeResolver);
		
		interceptors = (HandlerInterceptor[]) ObjectUtils.addObjectToArray(
				interceptors, publishedPageInterceptor);
	}
	
	public HandlerExecutionChain getHandler(HttpServletRequest request) 
			throws Exception {
		
		String path = servletMappingHelper.getLookupPathForRequest(request);
		if (log.isDebugEnabled()) {
			log.debug("Looking up handler for [" + path + "]");
		}
		PageAndController pc = pageMap.getPageAndController(path);
		if (pc == null) {
			return null;
		}
		
		Page page = pc.getPage();
		exposePage(page, request);
		return new HandlerExecutionChain(pc.getController(), interceptors);
	}
	
	protected void exposePage(Page page, HttpServletRequest request) {
		PageUtils.exposePage(request, page);
		PageUtils.exposePageMap(request, pageMap);
		if (pageAttribute != null) {
			request.setAttribute(pageAttribute, page);
		}
	}
	
}
