package org.riotfamily.search.index.support;

import org.riotfamily.search.crawler.support.LinkUtils;
import org.riotfamily.search.index.PagePreparator;
import org.riotfamily.search.parser.Page;

public class DefaultPagePreparator implements PagePreparator {

	private String stripTitlePrefix;
	
	private boolean stripHost;
	
	public void setStripTitlePrefix(String stripTitlePrefix) {
		this.stripTitlePrefix = stripTitlePrefix;
	}
	
	public void setStripHost(boolean stripHost) {
		this.stripHost = stripHost;
	}

	public void preparePage(Page page) {
		page.setUrl(prepareUrl(page.getUrl()));
		String title = page.getTitle();
		if (stripTitlePrefix != null && title != null) {
			if (title.startsWith(stripTitlePrefix)) {
				page.setTitle(title.substring(stripTitlePrefix.length()));
			}
		}
	}
	
	protected String prepareUrl(String url) {
		url = LinkUtils.stripSessionId(url);
		if (stripHost) {
			url = LinkUtils.stripHost(url);
		}
		return url;
	}

}
