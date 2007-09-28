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
	*   Carsten Woelk [cwoelk at neteye dot de]
	*
	* ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.macro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.context.PageRequestUtils;
import org.riotfamily.components.editor.EditModeUtils;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageHandlerMapping;
import org.riotfamily.pages.mapping.PageUrlBuilder;

/**
	* @author Felix Gnass [fgnass at neteye dot de]
	* @since 6.5
	*/
public class PageMacroHelper {

	private static final String PAGE_REFRESHED = PageMacroHelper.class.getName()
			+ ".refreshedPage";

	private PageDao pageDao;

	private PageUrlBuilder pageUrlBuilder;

	private HttpServletRequest request;

	public PageMacroHelper(PageDao pageDao, PageUrlBuilder pageUrlBuilder,
			HttpServletRequest request) {

		this.pageDao = pageDao;
		this.pageUrlBuilder = pageUrlBuilder;
		this.request = request;
	}

	public Page getCurrentPage() {
		Page page = PageHandlerMapping.getPage(request);
		if (PageRequestUtils.isPartialRequest(request)
				&& request.getAttribute(PAGE_REFRESHED) == null) {

			pageDao.refreshPage(page);
			request.setAttribute(PAGE_REFRESHED, Boolean.TRUE);
		}
		return page;
	}

	public Page getPageForHandler(String handlerName, Site site) {
		return pageDao.findPageForHandler(handlerName, site);
	}

	public List getPagesForHandler(String handlerName, Site site) {
		return pageDao.findPagesForHandler(handlerName, site);
	}

	public Collection getTopLevelPages(Site site) {
		return pageDao.getRootNode().getChildPages(site);
	}

	public String getUrl(Page page) {
		if (page != null) {
			return pageUrlBuilder.getUrl(page);
		}
		return null;
	}

	public boolean isVisible(Page page) {
		return !page.isHidden() && !page.getNode().isHidden()
				&& (page.isEnabled() || EditModeUtils.isEditMode(request));
	}

	public Collection getVisiblePages(Collection pages) {
		ArrayList result = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (isVisible(page)) {
				result.add(page);
			}
		}
		return result;
	}

	public List group(Collection pages, int size) {
		ArrayList groups = new ArrayList();
		int i = 0;
		ArrayList group = null;
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (isVisible(page)) {
				if (i++ % size == 0) {
					group = new ArrayList();
					groups.add(group);
				}
				group.add(page);
			}
		}
		return groups;
	}
}
