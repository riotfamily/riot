package org.riotfamily.linkcheck;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.crawler.Href;
import org.riotfamily.crawler.LinkExtractor;
import org.riotfamily.crawler.PageData;
import org.riotfamily.crawler.PageHandler;

public class LinkChecker implements PageHandler {
	
	private RiotLog log = RiotLog.get(this);

	private HttpStatusChecker statusChecker = new HttpStatusChecker();
	
	private LinkExtractor linkExtractor = new LinkCheckLinkExtractor();
	
	private HashSet<Link> brokenLinks;
	
	private HashSet<String> checkedUrls;
	
	private HashSet<Href> hrefsToCheck;
	
	public void crawlerStarted() {
		brokenLinks = new HashSet<Link>();
		checkedUrls = new HashSet<String>();
		hrefsToCheck = new HashSet<Href>();
	}
	
	public void handlePage(PageData pageData) {
		checkedUrls.add(pageData.getUrl());
		hrefsToCheck.remove(pageData.getHref());
		if (pageData.getStatusCode() >= 400) {
			Link link = new Link(pageData);
			log.info("Broken link: " + link);
			brokenLinks.add(link);
		}
		else {
			Collection<String> links = linkExtractor.extractLinks(pageData);
			Iterator<String> it = links.iterator();
			while (it.hasNext()) {
				String link = it.next();
				Href href = new Href(pageData.getUrl(), link);
				if (!checkedUrls.contains(href.getResolvedUri())) {
					hrefsToCheck.add(href);
				}
			}
		}
	}
	
	public void crawlerFinished() {
		log.info("Checking links that have not been crawled ...");
		Iterator<Href> it = hrefsToCheck.iterator();
		while (it.hasNext()) {
			Href href = it.next();
			Link link = new Link(href);
			if (!statusChecker.isOkay(link)) {
				log.debug("Broken link: " + link);
				brokenLinks.add(link);	
			}
		}
		
		Link.deleteAll();
		Link.saveAll(brokenLinks);
		
		checkedUrls = null;
		hrefsToCheck = null;
		brokenLinks = null;
	}
	
	public void handlePageIncremental(PageData pageData) {
		if (pageData.getStatusCode() < 400) {
			Link.deleteBrokenLinksFrom(pageData.getUrl());
			Link.deleteBrokenLinksTo(pageData.getUrl());
			HashSet<Link> brokenLinks = new HashSet<Link>();
			Collection<String> links = linkExtractor.extractLinks(pageData);
			Iterator<String> it = links.iterator();
			while (it.hasNext()) {
				String uri = it.next();
				Link link = new Link(pageData.getUrl(), uri);
				if (!statusChecker.isOkay(link)) {
					brokenLinks.add(link);
				}
			}
			Link.saveAll(brokenLinks);
		}
	}	

}
