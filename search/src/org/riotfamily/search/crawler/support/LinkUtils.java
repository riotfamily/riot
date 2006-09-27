package org.riotfamily.search.crawler.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public final class LinkUtils {

	private static Pattern jsessionIdPattern = 
			Pattern.compile(";jsessionid=[^?#]*");
	
	private static Pattern hostPattern = Pattern.compile("^http://[^/]*");
	
	private static Pattern fragmentPattern = Pattern.compile("#.*$");
	
	private LinkUtils() {
	}

	public static String stripSessionId(String url) {
		return jsessionIdPattern.matcher(url).replaceAll("");
	}

	public static String stripHost(String url) {
		return hostPattern.matcher(url).replaceAll("");
	}
	
	public static String stripFragment(String url) {
		return fragmentPattern.matcher(url).replaceAll("");
	}
	
	public static String resolveLink(String baseUrl, String href) {
		try {
			URI baseURI = new URI(baseUrl);
			return baseURI.resolve(href).toString();
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalArgumentException e) {
			return href;
		}
	}
	
}
