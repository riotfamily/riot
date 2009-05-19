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
package org.riotfamily.pages.schema;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.beans.factory.InitializingBean;

public class SitemapSchema implements InitializingBean {

	private List<TypeInfo> types;
	
	private List<SystemPage> pages;
	
	private Map<String, TypeInfo> typeMap = Generics.newHashMap();
	
	private List<TypeInfo> typesRefs = Generics.newArrayList();
	
	public void setTypes(List<TypeInfo> types) {
		this.types = types;
	}
	
	public List<TypeInfo> getTypes() {
		return types;
	}
	
	public void setPages(List<SystemPage> pages) {
		this.pages = pages;
	}
	
	public List<SystemPage> getPages() {
		return pages;
	}
	
	public void afterPropertiesSet() throws Exception {
		registerTypes(types);
		registerSystemPageTypes(pages);
		resolveTypeRefs();
	}

	private void registerTypes(List<TypeInfo> types) {
		if (types != null) {
			for (TypeInfo type : types) {
				registerType(type);
			}
		}
	}
	
	private void registerSystemPageTypes(List<SystemPage> pages) {
		if (pages != null) {
			for (SystemPage page : pages) {
				registerType(page.getType());
				registerSystemPageTypes(page.getPages());
			}
		}
	}

	private void registerType(TypeInfo type) {
		if (type.getRef() != null) {
			typesRefs.add(type);
		}
		else {
			if (typeMap.put(type.getName(), type) != null) {
				throw new IllegalArgumentException("Duplicate type: " + type.getName());
			}
			registerTypes(type.getChildTypes());
		}
	}
	
	private void resolveTypeRefs() {
		for (TypeInfo type : typesRefs) {
			type.resolve(typeMap);
		}
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
		if (pages != null) {
			for (SystemPage systemPage : pages) {
				systemPage.sync(site);
			}
		}
	}

	public List<TypeInfo> getChildTypeOptions(Page parentPage) {
		if (parentPage == null) {
			return types;
		}
		TypeInfo typeInfo = typeMap.get(parentPage.getPageType());
		return typeInfo != null ? typeInfo.getChildTypes() : null;
	}
	
}
