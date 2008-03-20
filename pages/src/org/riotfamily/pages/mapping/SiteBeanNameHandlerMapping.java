package org.riotfamily.pages.mapping;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.mapping.AdvancedBeanNameHandlerMapping;
import org.riotfamily.pages.model.Site;

public class SiteBeanNameHandlerMapping extends AdvancedBeanNameHandlerMapping {

	private PageResolver pageResolver;
	
	public SiteBeanNameHandlerMapping(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	protected Map getDefaults(HttpServletRequest request) {
		Site site = pageResolver.getSite(request);
		Map defaults = null;
		if (site != null) {
			String sitePrefix = site.getPathPrefix();
			if (sitePrefix == null) {
				sitePrefix = "";
			}
			defaults = Collections.singletonMap("sitePrefix", sitePrefix);
		}
		return defaults;
	}
}
