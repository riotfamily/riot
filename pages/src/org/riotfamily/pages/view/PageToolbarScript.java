package org.riotfamily.pages.view;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.view.DynamicToolbarScript;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageToolbarScript implements DynamicToolbarScript {

	public String generateJavaScript(HttpServletRequest request) {
		Page page = PageResolver.getResolvedPage(request);
		if (page != null) {
			return String.format(
					"riotComponentFormParams.pageId = %s;\n" +
					"riotComponentFormParams.siteId = %s;\n" +
					"riotContainerIds=[%s];\n", 
					page.getId(), 
					page.getSite().getId(), 
					page.getPageProperties().getId());
		}
		return null;
	}
	
}
