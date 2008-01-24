package org.riotfamily.search.site;

import org.riotfamily.crawler.PageData;
import org.riotfamily.search.index.Indexer;

public class SiteIndexer extends Indexer {

	private SiteIdentifier siteIdentifier;
	
	public SiteIndexer(SiteIdentifier siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

	public void crawlerStarted() {
		super.crawlerStarted();
		siteIdentifier.updateSiteList();
	}
	
	public void handlePageIncremental(PageData pageData) {
		siteIdentifier.updateSiteList();
		super.handlePageIncremental(pageData);
	}

}
