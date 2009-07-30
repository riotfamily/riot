package org.riotfamily.search.site;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.crawler.LinkFilter;
import org.riotfamily.pages.model.Site;

/**
 * {@link LinkFilter} that only accepts links that refer to a {@link Site}.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteLinkFilter implements LinkFilter {

	private SiteIdentifier siteIdentifier;
	
	public SiteLinkFilter(SiteIdentifier siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

	public boolean accept(String base, String href) {
		if (!ServletUtils.isAbsoluteUrl(href)) {
			// Always follow relative links
			return true;
		}
		String host = ServletUtils.getHost(href);
		if (host == null) {
			// Scheme but no host - must be something link javascript: or mailto:
			return false;
		}
		if (base != null) {
			String baseHost = ServletUtils.getHost(base);
			if (host.equals(baseHost)) {
				// Absolute link to same host so we follow it ...
				return true;
			}
		}
		// Other host - check if it belongs to one of our sites ...
		return siteIdentifier.isSiteHost(host);
	}
}
