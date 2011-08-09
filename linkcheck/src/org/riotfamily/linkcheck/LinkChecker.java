/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.linkcheck;

import java.util.HashSet;

import org.riotfamily.crawler.DefaultLinkExtractor;
import org.riotfamily.crawler.Href;
import org.riotfamily.crawler.LinkExtractor;
import org.riotfamily.crawler.LinkFilter;
import org.riotfamily.crawler.PageData;
import org.riotfamily.crawler.PageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class LinkChecker implements PageHandler {
	
	private Logger log = LoggerFactory.getLogger(LinkChecker.class);

	private HttpStatusChecker statusChecker = new HttpStatusChecker();
	
	private LinkExtractor linkExtractor = new DefaultLinkExtractor();

	private LinkFilter linkFilter = new LinkCheckLinkFilter();

	private HashSet<BrokenLink> brokenLinks;
	
	private HashSet<String> checkedUrls;
	
	private HashSet<Href> hrefsToCheck;
	
	public void crawlerStarted() {
		brokenLinks = new HashSet<BrokenLink>();
		checkedUrls = new HashSet<String>();
		hrefsToCheck = new HashSet<Href>();
	}
	
	public void handlePage(PageData pageData) {
		checkedUrls.add(pageData.getUrl());
		hrefsToCheck.remove(pageData.getHref());
		if (pageData.getStatusCode() >= 400) {
			BrokenLink link = new BrokenLink(pageData);
			log.info(String.format("Broken link: %s", link));
			brokenLinks.add(link);
		}
		else {
			for (String link : linkExtractor.extractLinks(pageData)) {
				if (linkFilter.accept(pageData.getUrl(), link)) {
					Href href = new Href(pageData.getUrl(), link, pageData.getHref().getResolvedUri());
					if (!checkedUrls.contains(href.getResolvedUri())) {
						hrefsToCheck.add(href);
					}
				}
			}
		}
	}
	
	@Transactional
	public void crawlerFinished() {
		log.info("Checking links that have not been crawled ...");
		for (Href href : hrefsToCheck) {
			BrokenLink link = new BrokenLink(href);
			if (!statusChecker.isOkay(link)) {
				log.info(String.format("Broken link: %s", link));
				brokenLinks.add(link);	
			}
		}
		
		BrokenLink.deleteAll();
		BrokenLink.saveAll(brokenLinks);
		
		log.info("Finished checking all links");

		checkedUrls = null;
		hrefsToCheck = null;
		brokenLinks = null;
	}
	
	public void handlePageIncremental(PageData pageData) {
		if (pageData.getStatusCode() < 400) {
			BrokenLink.deleteBrokenLinksFrom(pageData.getUrl());
			BrokenLink.deleteBrokenLinksTo(pageData.getUrl());
			HashSet<BrokenLink> brokenLinks = new HashSet<BrokenLink>();
			for (String uri : linkExtractor.extractLinks(pageData)) {
				if (linkFilter.accept(pageData.getUrl(), uri)) {
					BrokenLink link = new BrokenLink(pageData.getUrl(), uri);
					if (!statusChecker.isOkay(link)) {
						log.info(String.format("Broken link: %s", link));
						brokenLinks.add(link);
					}
				}
			}
			BrokenLink.saveAll(brokenLinks);
		}
	}	

}
