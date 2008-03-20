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
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PageChooserController implements Controller {

	public static final String PAGE_ID_PARAM = "pageId";
	
	public static final String SITE_ID_PARAM = "site";
	
	public static final String CROSS_SITE_PARAM = "crossSite";
	
	private PageDao pageDao;

	private PathCompleter pathCompleter;

	private String viewName = ResourceUtils.getPath(
			PageChooserController.class, "PageChooserView.ftl");


	public PageChooserController(PageDao pageDao, PathCompleter pathCompleter) {
		this.pageDao = pageDao;
		this.pathCompleter = pathCompleter;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Site selectedSite = null;
		Long siteId = ServletRequestUtils.getLongParameter(request, SITE_ID_PARAM);
		if (siteId != null) {
			selectedSite = pageDao.loadSite(siteId);
		}
		
		String path = null;
		boolean absolute = false;
		Page currentPage = null;
		Long pageId = ServletRequestUtils.getLongParameter(request, PAGE_ID_PARAM);
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
		
		if (currentPage != null && selectedSite.getHostName() != null) {
			absolute = !selectedSite.getHostName().equals(currentPage.getSite().getHostName());
		}
		
		HashMap model = new HashMap();
		model.put("mode", ServletRequestUtils.getStringParameter(
				request, "mode", "forms"));

		boolean crossSite = ServletRequestUtils.getBooleanParameter(
				request, CROSS_SITE_PARAM, false);
		
		if (crossSite) {
			model.put("sites", pageDao.listSites());
		}
		model.put("selectedSite", selectedSite);
		model.put("pages", createpageLinks(
				pageDao.getRootNode().getChildPages(selectedSite), 
				path, absolute, request));

		return new ModelAndView(viewName, model);
	}

	private List createpageLinks(Collection pages, String expandedPath,
				boolean absolute, HttpServletRequest request) {
		
		ArrayList links = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (!page.isWildcardInPath()) {
				PageLink link = new PageLink();
				link.setPathComponent(page.getPathComponent());
				if (absolute) {
					//FIXME!
					link.setLink(page.getAbsoluteUrl(pathCompleter, false,
							request.getServerName(), request.getContextPath(), 
							null));
				}
				else {
					link.setLink(page.getUrl(pathCompleter));
				}
				link.setTitle(page.getTitle(true));
				link.setPublished(page.isPublished());
				if (expandedPath != null && expandedPath.startsWith(page.getPath())) {
					link.setExpanded(true);
				}
				link.setChildPages(createpageLinks(page.getChildPages(),
						expandedPath, absolute, request));
				
				links.add(link);
			}
		}
		return links;
	}
}
