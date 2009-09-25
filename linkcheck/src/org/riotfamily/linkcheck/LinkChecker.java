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

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.crawler.DefaultLinkExtractor;
import org.riotfamily.crawler.Href;
import org.riotfamily.crawler.LinkExtractor;
import org.riotfamily.crawler.LinkFilter;
import org.riotfamily.crawler.PageData;
import org.riotfamily.crawler.PageHandler;
import org.springframework.transaction.annotation.Transactional;

public class LinkChecker implements PageHandler {
	
	private RiotLog log = RiotLog.get(this);

	private HttpStatusChecker statusChecker = new HttpStatusChecker();
	
	private LinkExtractor linkExtractor = new DefaultLinkExtractor();

	private LinkFilter linkFilter = new LinkCheckLinkFilter();

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
			log.info("Broken link: %s", link);
			brokenLinks.add(link);
		}
		else {
			for (String link : linkExtractor.extractLinks(pageData)) {
				if (linkFilter.accept(pageData.getUrl(), link)) {
					Href href = new Href(pageData.getUrl(), link);
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
			Link link = new Link(href);
			if (!statusChecker.isOkay(link)) {
				log.info("Broken link: %s", link);
				brokenLinks.add(link);	
			}
		}
		
		Link.deleteAll();
		Link.saveAll(brokenLinks);
		
		log.info("Finished checking all links");

		checkedUrls = null;
		hrefsToCheck = null;
		brokenLinks = null;
	}
	
	public void handlePageIncremental(PageData pageData) {
		if (pageData.getStatusCode() < 400) {
			Link.deleteBrokenLinksFrom(pageData.getUrl());
			Link.deleteBrokenLinksTo(pageData.getUrl());
			HashSet<Link> brokenLinks = new HashSet<Link>();
			for (String uri : linkExtractor.extractLinks(pageData)) {
				if (linkFilter.accept(pageData.getUrl(), uri)) {
					Link link = new Link(pageData.getUrl(), uri);
					if (!statusChecker.isOkay(link)) {
						log.info("Broken link: %s", link);
						brokenLinks.add(link);
					}
				}
			}
			Link.saveAll(brokenLinks);
		}
	}	

}
