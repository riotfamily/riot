/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.search.crawler;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.search.crawler.support.DefaultCrawlerModel;
import org.riotfamily.search.crawler.support.LinkUtils;


public class Crawler {

	private Log log = LogFactory.getLog(Crawler.class);
	
	private String initialUrl;
	
	private CrawlerModel model;
	
	private LinkExtractor linkExtractor;
	
	private PageLoader pageLoader;
	
	private LinkFilter linkFilter;

	
	public void setInitialUrl(String initialUrl) {
		this.initialUrl = initialUrl;
	}

	public void setLinkExtractor(LinkExtractor linkExtractor) {
		this.linkExtractor = linkExtractor;
	}

	public void setLinkFilter(LinkFilter linkFilter) {
		this.linkFilter = linkFilter;
	}

	public void setModel(CrawlerModel model) {
		this.model = model;
	}

	public void setPageLoader(PageLoader pageLoader) {
		this.pageLoader = pageLoader;
	}
	
	public void crawl() {
		if (model == null) {
			model = new DefaultCrawlerModel();
		}
		
		int pageCount = 0;
		long startTime = System.currentTimeMillis();
		
	    model.addUrl(initialUrl);
	    
	    while (model.hasNextUrl()) {
	        String pageUrl = model.getNextUrl();
	        PageData pageData = pageLoader.loadPage(pageUrl);
	        if (pageData.isOk()) {
	            Collection links = linkExtractor.extractLinks(pageData);
	            Iterator it = links.iterator();
	            while (it.hasNext()) {
					addUrlToModel(pageUrl, (String) it.next());
				}
	        }
	        else if (pageData.isRedirect()) {
	        	log.debug("Redirect: " + pageData.getRedirectUrl());
	        	addUrlToModel(pageUrl, pageData.getRedirectUrl());
	        }
	        pageCount++;
	    }
	    
	    log.info(pageCount + " pages crawled in " + 
	    		(System.currentTimeMillis() - startTime) + " ms");
	}
	
	protected void addUrlToModel(String baseUrl, String link) {
		link = normalizeLink(baseUrl, link);
		if (linkFilter == null || linkFilter.accept(baseUrl, link)) {
			model.addUrl(link);
		}
	}
	
	protected String normalizeLink(String baseUrl, String href) {
		String link = LinkUtils.resolveLink(baseUrl, href);
		return LinkUtils.stripFragment(link);
	}
	
}
