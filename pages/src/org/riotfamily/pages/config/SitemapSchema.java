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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

public class SitemapSchema {

	private String defaultSuffix;
	
	private Map<String, PageType> typeMap = Generics.newHashMap();
		
	private List<SystemPage> systemPages = Generics.newArrayList();
	
	private List<PageType> rootTypes = Generics.newArrayList();

	public String getDefaultSuffix() {
		return defaultSuffix;
	}

	public void setDefaultSuffix(String defaultSuffix) {
		this.defaultSuffix = defaultSuffix;
	}

	public void setTypes(List<PageType> types) {
		if (types != null) {
			for (PageType type : types) {
				type.register(this);
				if (!(type instanceof SystemPage)) {
					rootTypes.add(type);
				}
			}
		}
	}
	
	void addType(PageType type) {
		if (typeMap.put(type.getName(), type) != null) {
			throw new IllegalArgumentException("Duplicate type: " + type.getName());
		}
	}
	
	public PageType getPageType(String name) {
		return typeMap.get(name);
	}
	
	void addSystemPage(SystemPage page) {
		systemPages.add(page);
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
		if (systemPages != null) {
			for (SystemPage systemPage : systemPages) {
				systemPage.sync(site);
			}
		}
	}

	public List<PageType> getChildTypeOptions(Page parentPage) {
		if (parentPage == null) {
			return rootTypes;
		}
		return getPageType(parentPage.getPageType()).getChildTypes();
	}

	public String getDefaultSuffix(String pageType) {
		List<String> suffixes = getPageType(pageType).getSuffixes();
		if (suffixes != null && !suffixes.isEmpty()) {
			return suffixes.get(0);
		}
		return defaultSuffix;
	}

	public boolean isSystemPage(String pageType) {
		return getPageType(pageType) instanceof SystemPage;
	}

	public boolean isValidPath(String path, String pageType) {
		return true; //TODO
	}
	
}
