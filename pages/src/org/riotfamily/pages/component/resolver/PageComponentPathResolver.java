package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletMappingHelper;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;
import org.riotfamily.pages.setup.WebsiteConfigSupport;

public class PageComponentPathResolver extends WebsiteConfigSupport 
		implements ComponentPathResolver {

	private static ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper(true);
	
	public String getComponentPath(HttpServletRequest request) {
		return servletMappingHelper.getLookupPathForRequest(request);
	}
	
	public String getParentPath(String path) {
		Page page = getPageMap().getPage(path);
		Page parent = page.getParent();
		if (parent != null) {
			if (parent.isFolder()) {
				Page indexPage = PageUtils.getFirstChild(parent);
				if (page.equals(indexPage)) {
					return getParentPath(parent.getPath());
				}
				else {
					parent = indexPage;
				}
			}
			return parent.getPath();
		}
		return null;
	}

}
