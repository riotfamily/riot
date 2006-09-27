package org.riotfamily.search.crawler.support;

import java.util.HashSet;
import java.util.Stack;

import org.riotfamily.search.crawler.CrawlerModel;

public class DefaultCrawlerModel implements CrawlerModel {

	private HashSet knownUrls = new HashSet();

	private Stack urls = new Stack();
	
	public boolean hasNextUrl() {
		return !urls.isEmpty();
	}

	public String getNextUrl() {
		return (String) urls.pop();
	}

	public void addUrl(String url) {
		String lookupUrl = normalizeUrl(url);
		if (!knownUrls.contains(lookupUrl)) {
			knownUrls.add(lookupUrl);
			urls.push(url);
		}
	}

	protected String normalizeUrl(String url) {
		return LinkUtils.stripSessionId(url);
	}

}
