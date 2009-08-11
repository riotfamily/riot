package org.riotfamily.pages.mapping;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.model.Site;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

public class NoSiteHandlerMapping extends AbstractHandlerMapping {

	private PageResolver pageResolver;
	
	private Object siteNotFoundHandler;

	public NoSiteHandlerMapping(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	public void setSiteNotFoundHandler(Object siteNotFoundHandler) {
		this.siteNotFoundHandler = siteNotFoundHandler;
	}
	
	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		
		Site site = pageResolver.getSite(request);
		if (site == null) {
			return siteNotFoundHandler;
		}
		return null;
	}
	
}
