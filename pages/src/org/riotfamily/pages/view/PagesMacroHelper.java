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
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.view;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.pages.cache.PageCacheUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.PageProperties;
import org.riotfamily.pages.model.Site;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.6
 */
public class PagesMacroHelper {

	private ComponentDao componentDao;
	
	private PageDao pageDao;
	
	private PageResolver pageResolver;

	private HttpServletRequest request;

	
	public PagesMacroHelper(ComponentDao componentDao, PageDao pageDao, 
			PageResolver pageResolver, HttpServletRequest request) {
		
		this.componentDao = componentDao;
		this.pageDao = pageDao;
		this.pageResolver = pageResolver;
		this.request = request;
	}
	
	
	public List<Page> getTopLevelPages(Site site) {
		PageNode rootNode = pageDao.getRootNode();
		PageCacheUtils.addNodeTag(rootNode);
		return getVisiblePages(rootNode.getChildPages(site),
				EditModeUtils.isEditMode(request));
	}

	public Page getPageForUrl(String url, Site site) {
		return pageResolver.resolvePage(url, request.getContextPath(), site);
	}
	
	public Page getPageOfType(String pageType, Site site) {
		return pageDao.findPageOfType(pageType, site);
	}

	public List<Page> getPagesOfType(String pageType, Site site) {
		return pageDao.findPagesOfType(pageType, site);
	}

	public Page getPageForComponent(Component component) {
		ContentContainer container = componentDao.findContainerForComponent(component);
		if (container instanceof PageProperties) {
			return ((PageProperties) container).getPage();
		}
		return null;
	}
	
	private List<Page> getVisiblePages(List<Page> pages, boolean preview) {
		ArrayList<Page> result = new ArrayList<Page>();
		for (Page page : pages) {
			if (page.isVisible(preview)) {
				result.add(page);
			}
		}
		return result;
	}

	public Site getSiteWithProperty(String name, Object value) {
		return this.pageDao.findSiteWithProperty(name, value);
	}
}
