package org.riotfamily.search.site;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.crawler.DefaultLinkExtractor;
import org.riotfamily.crawler.LinkExtractor;
import org.riotfamily.pages.model.Site;

/**
 * {@link LinkExtractor} that only returns links that refer to a {@link Site}.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteLinkExtractor extends DefaultLinkExtractor {

	private SiteIdentifier siteIdentifier;
	
	public SiteLinkExtractor(SiteIdentifier siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

	protected boolean accept(String base, String uri) {
		if (!ServletUtils.isAbsoluteUrl(uri)) {
			// Always follow relative links
			return true;
		}
		String host = ServletUtils.getHost(uri);
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
