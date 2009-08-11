package org.riotfamily.pages;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.ChainedLocaleResolver;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SiteLocaleResolver extends ChainedLocaleResolver {

	private PageResolver pageResolver;
	
	public SiteLocaleResolver(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	protected Locale resolveLocaleInternal(HttpServletRequest request) {
		Site site = pageResolver.getSite(request);
		if (site != null) {
			return site.getLocale();
		}
		return null;
	}
	
}
