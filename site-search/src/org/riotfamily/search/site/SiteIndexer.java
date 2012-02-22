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
