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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.config;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class SitemapSchema {

	private String defaultSuffix;
	
	private RootPage rootPage;
	
	private Map<String, PageType> typeMap = Generics.newHashMap();
		
	public String getDefaultSuffix() {
		return defaultSuffix;
	}

	public void setDefaultSuffix(String defaultSuffix) {
		this.defaultSuffix = defaultSuffix;
	}

	public void setRootPage(RootPage rootPage) {
		this.rootPage = rootPage;
		rootPage.register(this);
	}
	
	void addType(PageType type) {
		if (typeMap.put(type.getName(), type) != null) {
			throw new IllegalArgumentException("Duplicate type: " + type.getName());
		}
	}
	
	public PageType getPageType(Page page) {
		return getPageType(page.getPageType());
	}
	
	public PageType getPageType(String name) {
		return typeMap.get(name);
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

	public List<PageType> getChildTypeOptions(Page parent) {
		List<PageType> options = null;
		if (parent instanceof Page) {
			options = getPageType((Page) parent).getChildTypes();
		}
		if (options == null) {
			options = Collections.emptyList();
		}
		return options;
	}

	public String getDefaultSuffix(String pageType) {
		List<String> suffixes = getPageType(pageType).getSuffixes();
		if (suffixes != null && !suffixes.isEmpty()) {
			return suffixes.get(0);
		}
		return defaultSuffix;
	}

	public boolean isSystemPage(Page page) {
		return getPageType(page) instanceof SystemPage;
	}

	public boolean canHaveChildren(Page parent) {
		return !getChildTypeOptions(parent).isEmpty();
	}
	
	public boolean isValidChild(Page parent, Page child) {
		return getChildTypeOptions(parent).contains(getPageType(child));
	}
	
	public boolean suffixMatches(Page page, String path) {
		String suffix = null;
		int i = path.lastIndexOf(page.getPathComponent()) + page.getPathComponent().length();
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
