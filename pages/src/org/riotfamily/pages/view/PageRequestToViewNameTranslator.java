package org.riotfamily.pages.view;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.springframework.web.servlet.RequestToViewNameTranslator;

/**
 * RequestToViewNameTranslator that uses the pageType of the resolved Page
 * to construct a viewName.
 * 
 * @see PageResolver
 * @since 8.0
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class PageRequestToViewNameTranslator 
		implements RequestToViewNameTranslator {

	private String prefix = "";
	
	private String suffix = "";
	
	private String defaultPageType = "default";
	
	private RequestToViewNameTranslator noPageTranslator;
	
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setDefaultPageType(String defaultPageType) {
		this.defaultPageType = defaultPageType;
	}
	
	public void setNoPageTranslator(RequestToViewNameTranslator noPageTranslator) {
		this.noPageTranslator = noPageTranslator;
	}

	public String getViewName(HttpServletRequest request) throws Exception {
		Page page = PageResolver.getResolvedPage(request);
		if (page != null) {
			String pageType = page.getPageType();
			if (pageType == null) {
				pageType = defaultPageType;
			}
			return prefix + pageType + suffix;
		}
		else if (noPageTranslator != null) {
			return noPageTranslator.getViewName(request);
		}
		return null;
	}

}
