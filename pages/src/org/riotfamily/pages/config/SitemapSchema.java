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
package org.riotfamily.pages.config;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.riotfamily.common.util.Generics;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class SitemapSchema {

	private static SitemapSchema defaultSchema = new SitemapSchema();
	
	private String defaultSuffix;
	
	private RootPageType rootPage;
	
	private Map<String, PageType> typeMap = Generics.newHashMap();
	
	private Set<String> virtualParents = Generics.newHashSet();
	
	public static SitemapSchema getDefault() {
		return defaultSchema;
	}
	
	public String getDefaultSuffix() {
		return defaultSuffix;
	}

	public void setDefaultSuffix(String defaultSuffix) {
		this.defaultSuffix = defaultSuffix;
	}

	public void setRootPage(RootPageType rootPage) {
		this.rootPage = rootPage;
		rootPage.register(this, null);
	}
	
	void addType(PageType type) {
		if (typeMap.put(type.getName(), type) != null) {
			throw new IllegalArgumentException("Duplicate type: " + type.getName());
		}
		if (isVirtualParent(type)) {
			virtualParents.add(type.getName());
		}
	}
	
	private boolean isVirtualParent(PageType type) {
		if (type instanceof SystemPageType) {
			return ((VirtualPageParent) type).getVirtualChildType() != null;
		}
		return false;
	}

	public PageType getPageType(Page page) {
		return getPageType(page.getPageType());
	}
	
	public PageType getPageType(String name) {
		return typeMap.get(name);
	}
	
	public Set<String> getVirtualParents() {
		return virtualParents;
	}
	
	void syncSystemPages() {
		List<Site> sites = Site.findAll();
		if (sites.isEmpty()) {
			Site site = new Site();
			site.setName("Default");
			site.setLocale(Locale.getDefault());
			site.save();
			syncSystemPages(site);
		}
		else {
			for (Site site : sites) {
				syncSystemPages(site);
			}
		}
	}
	
	void syncSystemPages(Site site) {
		rootPage.sync(site);
	}

	public List<? extends PageType> getChildTypeOptions(ContentPage parent) {
		return Generics.emptyIfNull(getPageType(parent).getChildTypes());
	}
	
	public VirtualPageType getVirtualChildType(Page page) {
		PageType parentType = getPageType(page);
		if (parentType instanceof VirtualPageParent) {
			return ((VirtualPageParent) parentType).getVirtualChildType();
		}
		return null;
	}

	public String getDefaultSuffix(String pageType) {
		List<String> suffixes = getPageType(pageType).getSuffixes();
		if (suffixes != null && !suffixes.isEmpty()) {
			return suffixes.get(0);
		}
		return defaultSuffix;
	}

	public boolean isSystemPage(ContentPage page) {
		return getPageType(page) instanceof SystemPageType;
	}

	public boolean canHaveChildren(ContentPage parent) {
		return !getChildTypeOptions(parent).isEmpty();
	}
	
	public boolean isValidChild(ContentPage parent, ContentPage child) {
		return getChildTypeOptions(parent).contains(getPageType(child));
	}
	
	public boolean suffixMatches(Page page, String path) {
		String suffix = null;
		int i = page.getPath().length();
		if (i < path.length()) {
			suffix = path.substring(i);
		}
		List<String> suffixes = getPageType(page).getSuffixes();
		if (suffixes != null && !suffixes.isEmpty()) {
			for (String s : suffixes) {
				if (nullSafeEquals(suffix, s)) {
					return true;
				}
			}
			return false;
		}
		return nullSafeEquals(suffix, defaultSuffix);
	}
	
	private static boolean nullSafeEquals(String s1, String s2) {
		return ObjectUtils.nullSafeEquals(s1, s2)
				|| (!StringUtils.hasText(s1) && !StringUtils.hasText(s2));
	}

}
