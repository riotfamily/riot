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
package org.riotfamily.pages.view;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.website.cache.CacheTagUtils;

public class SiteFacade {
	
	private Site site;

	private HttpServletRequest request;
	
	private Map<String, Object> properties = null;

	public SiteFacade(Site site, HttpServletRequest request) {
		this.site = site;
		this.request = request;
		CacheTagUtils.tag(site);
	}
		
	public String getAbsoluteUrl() {
		return makeAbsolute("/");
	}
	
	public String makeAbsolute(String path) {
		return site.makeAbsolute(request.isSecure(),
				ServletUtils.getServerNameAndPort(request), 
				request.getContextPath(), path);
	}

	public Set<String> getAliases() {
		return site.getAliases();
	}

	public Set<Site> getDerivedSites() {
		return site.getDerivedSites();
	}

	public String getHostName() {
		return site.getHostName();
	}

	public Long getId() {
		return site.getId();
	}

	public Locale getLocale() {
		return site.getLocale();
	}

	public Site getMasterSite() {
		return site.getMasterSite();
	}

	public String getName() {
		return site.getName();
	}

	public boolean isEnabled() {
		return site.isEnabled();
	}
	
	public Page getRootPage() {
		return site.getRootPage();
	}
	
	public Map<String, Object> getProperties() {
		if (properties == null) {
			properties = site.getPropertiesMap();
		}
		return properties;
	}
	
	/**
	 * @see http://freemarker.org/docs/api/freemarker/ext/beans/BeanModel.html#get(java.lang.String)
	 */
	public Object get(String key) {
		return site.getProperty(key);
	}
	
	public Map<String, Object> getLocal() {
		return site.getProperties();
	}

	public String toString() {
		return site.toString();
	}
	
}