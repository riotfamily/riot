package org.riotfamily.search.crawler.support;

import java.net.URI;
import java.util.regex.Pattern;

import org.riotfamily.search.crawler.LinkFilter;

public class LocalLinkFilter implements LinkFilter {

	private Pattern pathPattern;
	
	public LocalLinkFilter() {
	}

	public LocalLinkFilter(String pathPattern) {
		if (pathPattern != null) {
			this.pathPattern = Pattern.compile(pathPattern);
		}
	}
	
	public boolean accept(String baseUrl, String link) {
		try {
			URI baseURI = new URI(baseUrl);
			URI linkURI = baseURI.resolve(link);
			if (isValidHost(baseURI, linkURI)) {
				if (pathPattern != null) {
					return pathPattern.matcher(linkURI.getPath()).matches();
				}
				else {
					return true;
				}
			}
		}
		catch (Exception e) {
		}
		return false;
	}
	
	protected boolean isValidHost(URI baseURI, URI linkURI) {
		return baseURI.getHost().equals(linkURI.getHost());
	}

}
