package org.riotfamily.linkcheck;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.crawler.DefaultLinkExtractor;

public class LinkCheckLinkExtractor extends DefaultLinkExtractor {

	private static final String[] IGNORED_SCHEMES = { 
		"javascript:", "mailto:", "ftp:" 
	};
	
	protected boolean accept(String base, String uri) {
		return !ServletUtils.isAbsoluteUrl(uri) || !ignoreScheme(uri); 
	}
	
	protected boolean ignoreScheme(String uri) {
		for (int i = 0; i < IGNORED_SCHEMES.length; i++) {
			if (uri.startsWith(IGNORED_SCHEMES[i])) {
				return true;
			}
		}
		return false;
	}
}
