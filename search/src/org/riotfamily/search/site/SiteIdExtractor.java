/**
 * 
 */
package org.riotfamily.search.site;

import org.riotfamily.crawler.PageData;
import org.riotfamily.search.index.html.FieldExtractor;
import org.riotfamily.pages.model.Site;

/**
 * FieldExtractor that extracts the {@link Site#getId() siteId} from the URL.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteIdExtractor implements FieldExtractor {

	private SiteIdentifier siteIdentifier;
	
	public SiteIdExtractor(SiteIdentifier siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

	public String getFieldValue(PageData pageData) {
		Site site = siteIdentifier.getSiteForUrl(pageData.getUrl());
		return site != null ? site.getId().toString() : null;
	}
}