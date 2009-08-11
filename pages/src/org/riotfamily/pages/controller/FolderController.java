package org.riotfamily.pages.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.website.cache.CacheTagUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class FolderController implements CacheableController {

	public String getCacheKey(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}

	public long getLastModified(HttpServletRequest request) throws Exception {
		return System.currentTimeMillis();
	}

	public long getTimeToLive() {
		return CACHE_ETERNALLY;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Page page = PageResolver.getResolvedPage(request);
		CacheTagUtils.tag(page);
		for (Page child : page.getChildPages()) {
			if (child.isRequestable()) {
				return new ModelAndView(new RedirectView(child.getUrl(), true));
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}

	
}
