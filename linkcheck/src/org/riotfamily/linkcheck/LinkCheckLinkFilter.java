package org.riotfamily.linkcheck;

import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.crawler.LinkFilter;

public class LinkCheckLinkFilter implements LinkFilter {

	private static final String[] IGNORED_SCHEMES = { 
		"javascript:", "mailto:", "ftp:" 
	};
	
	public boolean accept(String base, String href) {
		return !ServletUtils.isAbsoluteUrl(href) || !ignoreScheme(href); 
	}
	
	protected boolean ignoreScheme(String href) {
		for (int i = 0; i < IGNORED_SCHEMES.length; i++) {
			if (href.startsWith(IGNORED_SCHEMES[i])) {
				return true;
			}
		}
		return false;
	}
}
