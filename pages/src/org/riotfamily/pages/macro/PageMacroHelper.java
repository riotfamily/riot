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
package org.riotfamily.pages.macro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.context.ComponentEditorRequest;
import org.riotfamily.components.editor.ComponentFormRegistry;
import org.riotfamily.components.editor.EditModeUtils;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageHandlerMapping;
import org.riotfamily.pages.mapping.PageLocationResolver;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageMacroHelper {

	private static final String PAGE_REFRESHED = PageMacroHelper.class.getName()
			+ ".refreshedPage";

	private PageDao pageDao;

	private PageLocationResolver resolver;

	private ComponentFormRegistry formRegistry;

	private HttpServletRequest request;

	public PageMacroHelper(PageDao pageDao, PageLocationResolver resolver,
			ComponentFormRegistry formRegistry, HttpServletRequest request) {

		this.pageDao = pageDao;
		this.resolver = resolver;
		this.formRegistry = formRegistry;
		this.request = request;
	}

	public Page getCurrentPage() {
		Page page = PageHandlerMapping.getPage(request);
		if (ComponentEditorRequest.isWrapped(request)
				&& request.getAttribute(PAGE_REFRESHED) == null) {

			pageDao.refreshPage(page);
			request.setAttribute(PAGE_REFRESHED, Boolean.TRUE);
		}
		return page;
	}

	public Page getPageForHandler(String handlerName, Locale locale) {
		return pageDao.findPageForHandler(handlerName, locale);
	}

	public List getPagesForHandler(String handlerName, Locale locale) {
		return pageDao.findPagesForHandler(handlerName, locale);
	}

	public Collection getTopLevelPages(Locale locale) {
		Site site = getCurrentPage().getNode().getSite();
		return pageDao.findRootNode(site).getChildPages(locale);
	}

	public String getUrl(Page page) {
		if (page != null) {
			return resolver.getUrl(new PageLocation(page));
		}
		return null;
	}

	public String getWildcardHandlerUrl(String handlerName,
			String wildcardReplacement, Locale locale) {

		Page page = getPageForHandler(handlerName, locale);
		if (page != null) {
			Assert.isTrue(page.isWildcardMapping(), "Page has no wildcard mapping");
			return resolver.getUrl(new PageLocation(page, wildcardReplacement));
		}
		return null;
	}

	public String getWildcardMatch() {
		return PageHandlerMapping.getWildcardMatch(request);
	}

	public String getFormUrl(String formId, Long containerId) {
		return formRegistry.getFormUrl(formId, containerId);
	}

	public List group(Collection pages, int size) {
		ArrayList groups = new ArrayList();
		int i = 0;
		ArrayList group = null;
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (!page.getNode().isHidden() && (page.isPublished()
					|| EditModeUtils.isEditMode(request))) {

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
