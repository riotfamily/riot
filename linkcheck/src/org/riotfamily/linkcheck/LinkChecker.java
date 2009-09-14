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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.crawler.Href;
import org.riotfamily.crawler.LinkExtractor;
import org.riotfamily.crawler.PageData;
import org.riotfamily.crawler.PageHandler;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional
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
