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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.page.support;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.PageMap;

public final class PageUtils {

	private static final String PAGE_ATTRIBUTE = 
			PageUtils.class.getName() + "page";
	
	private static final String PAGE_MAP_ATTRIBUTE = 
			PageUtils.class.getName() + ".pageMap";
	
	private PageUtils() {
	}

	public static void exposePage(HttpServletRequest request, Page page) {
		request.setAttribute(PAGE_ATTRIBUTE, page);
	}
	
	public static void exposePageMap(HttpServletRequest request, 
			PageMap pageMap) {
		
		request.setAttribute(PAGE_MAP_ATTRIBUTE, pageMap);
	}
	
	public static Page getPage(HttpServletRequest request) {
		return (Page) request.getAttribute(PAGE_ATTRIBUTE);
	}
	
	public static PageMap getPageMap(HttpServletRequest request) {
		return (PageMap) request.getAttribute(PAGE_MAP_ATTRIBUTE);
	}
	
	public static Collection getRootPages(HttpServletRequest request) {
		PageMap pageMap = getPageMap(request);
		return pageMap.getRootPages();
	}
	
	public static Page getRootPage(Page page) {
		while (page.getParent() != null) {
			page = page.getParent();
		}
		return page;
	}
	
	public static Page getFirstChild(Page page) {
		Collection childPages = page.getChildPages();
		if (childPages == null || childPages.isEmpty()) {
			return null;
		}
		return (Page) childPages.iterator().next();
	}
	
	public static Page getChild(Page page, String pathComponent) {
		return getPage(page.getChildPages(), pathComponent);
	}
	
	public static Page getPage(Collection pages, String pathComponent) {
		if (pages != null) {
			Iterator it = pages.iterator();
			while (it.hasNext()) {
				Page child = (Page) it.next();
				if (child.getPathComponent().equals(pathComponent)) {
					return child;
				}
			}
		}
		return null;
	}

}
