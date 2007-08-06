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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.ComponentListLocator;
import org.riotfamily.components.Location;
import org.riotfamily.components.locator.SlotResolver;
import org.riotfamily.components.locator.TemplateSlotResolver;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageHandlerMapping;
import org.riotfamily.pages.mapping.PageLocationResolver;
import org.springframework.util.StringUtils;

/**
 * ComponentListLocator that uses the page-id as component-path.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageComponentListLocator implements ComponentListLocator {

	public static final String TYPE_PAGE = "page";

	public static final String TYPE_WILDCARD_PAGE_PREFIX = "page-";

	private PageDao pageDao;

	private PageLocationResolver resolver;

	private SlotResolver slotResolver = new TemplateSlotResolver();

	public PageComponentListLocator(PageDao pageDao,
			PageLocationResolver resolver) {

		this.pageDao = pageDao;
		this.resolver = resolver;
	}

	public void setSlotResolver(SlotResolver slotResolver) {
		this.slotResolver = slotResolver;
	}

	public boolean supports(String type) {
		return type.equals(TYPE_PAGE) || type.startsWith(TYPE_WILDCARD_PAGE_PREFIX);
	}

	public Location getLocation(HttpServletRequest request) {
		Location location = new Location();
		Page page = PageHandlerMapping.getPage(request);
		if (page.isWildcardMapping()) {
			location.setType(TYPE_WILDCARD_PAGE_PREFIX + page.getId());
			location.setPath(PageHandlerMapping.getWildcardMatch(request));
		}
		else {
			location.setType(TYPE_PAGE);
			location.setPath(page.getId().toString());
		}
		location.setSlot(slotResolver.getSlot(request));
		return location;
	}

	public Location getParentLocation(Location location) {
		Page page = loadPage(location);
		Location parentLocation = new Location(location);
		location.setType(TYPE_PAGE);
		location.setPath(page.getParentPage().getId().toString());
		return parentLocation;
	}

	public String getUrl(Location location) {
		Page page = loadPage(location);
		String url = resolver.getUrl(page);
		if (page.isWildcardMapping()) {
			url = StringUtils.replace(url, "*", location.getPath());
		}
		return url;
	}

	private Page loadPage(Location location) {
		String id;
		if (location.getType().equals(TYPE_PAGE)) {
			 id = location.getPath();
		}
		else {
			String type = location.getType();
			int i = type.indexOf('-');
			id = type.substring(i + 1);
		}
		return pageDao.loadPage(new Long(id));
	}

}
