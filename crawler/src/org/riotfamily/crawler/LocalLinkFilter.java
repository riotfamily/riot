package org.riotfamily.crawler;

import org.riotfamily.common.servlet.ServletUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class LocalLinkFilter implements LinkFilter {

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
		return false;
	}

}
