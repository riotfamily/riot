/**
 * 
 */
package org.riotfamily.search.site;

import org.riotfamily.crawler.PageData;
import org.riotfamily.pages.model.Site;
import org.riotfamily.search.index.html.FieldExtractor;

/**
 * FieldExtractor that extracts the the language from the site's 
 * {@link Site#getLocale() locale}.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteLanguageExtractor implements FieldExtractor {

	private SiteIdentifier siteIdentifier;
	
	public SiteLanguageExtractor(SiteIdentifier siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

	public String getFieldValue(PageData pageData) {
		Site site = siteIdentifier.getSiteForUrl(pageData.getUrl());
		return site != null ? site.getLocale().getLanguage() : null;
	}
}