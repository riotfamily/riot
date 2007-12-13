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
package org.riotfamily.pages.riot.chooser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageUrlBuilder;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PageChooserController implements Controller {

	public static final String PAGE_ID_PARAM = "pageId";
	
	private PageDao pageDao;

	private PageUrlBuilder pageUrlBuilder;

	private String viewName = ResourceUtils.getPath(
			PageChooserController.class, "PageChooserView.ftl");


	public PageChooserController(PageDao pageDao, PageUrlBuilder pageUrlBuilder) {
		this.pageDao = pageDao;
		this.pageUrlBuilder = pageUrlBuilder;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Site selectedSite = null;
		Long siteId = ServletRequestUtils.getLongParameter(request, "site");
		if (siteId != null) {
			selectedSite = pageDao.loadSite(siteId);
		}
		
		String path = null;
		Page currentPage = null;
		Long pageId = ServletRequestUtils.getLongParameter(request, "pageId");
		if (pageId != null) {
			currentPage = pageDao.loadPage(pageId);
			path = currentPage.getPath();
			if (selectedSite == null) {
				selectedSite = currentPage.getSite();
			}
		}
		else if (selectedSite == null) {
			selectedSite = pageDao.getDefaultSite();
		}
		
		HashMap model = new HashMap();
		model.put("mode", ServletRequestUtils.getStringParameter(
				request, "mode", "forms"));

		model.put("sites", pageDao.listSites());
		model.put("selectedSite", selectedSite);
		model.put("pages", createpageLinks(
				pageDao.getRootNode().getChildPages(selectedSite), path, request));

		return new ModelAndView(viewName, model);
	}

	private List createpageLinks(Collection pages, String expandedPath,
				HttpServletRequest request) {
		
		ArrayList links = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (!page.isWildcardInPath()) {
				PageLink link = new PageLink();
				link.setPathComponent(page.getPathComponent());
				link.setLink(pageUrlBuilder.getUrl(page, request));
				link.setTitle(page.getProperty("title", true));
				link.setPublished(page.isPublished());
				if (expandedPath != null && expandedPath.startsWith(page.getPath())) {
					link.setExpanded(true);
				}
				link.setChildPages(createpageLinks(page.getChildPages(),
						expandedPath, request));
				
				links.add(link);
			}
		}
		return links;
	}
}
