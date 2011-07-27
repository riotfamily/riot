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

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.pages.model.Site;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Service that can be used to identify the Sites that belongs to an URL. 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteIdentifier {

	private PlatformTransactionManager transactionManager;
	
	private List<Site> sites;

	
	public SiteIdentifier(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void updateSiteList() {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus ts) {
				sites = Site.findAll();
				if (sites != null) {
				    Iterator<Site> i = sites.iterator();
				    while (i.hasNext()) {
				        Hibernate.initialize(i.next().getAliases());
				    }
				}
			}
		});
	}
	
	/**
	 * Returns the first Site that matches the given URL.
	 */	
	public Site getSiteForUrl(String url) {
		String hostName = ServletUtils.getHost(url);
		return getSite(hostName);
	}

	private Site getSite(String hostName) {
		Iterator<Site> it = sites.iterator();
		while (it.hasNext()) {
			Site site = it.next();
			if (site.hostNameMatches(hostName)) {
				return site;
			}
		}
		return null;
	}
	
	/**
	 * Returns whether the given hostName belongs to any of the configured
	 * Sites.
	 */
	public boolean isSiteHost(String hostName) {
		Iterator<Site> it = sites.iterator();
		while (it.hasNext()) {
			Site site = it.next();
			if (site.hostNameMatches(hostName, false)) {
				return true;
			}
		}
		return false;
	}
}
