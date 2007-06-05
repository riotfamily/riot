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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.transaction.TransactionalController;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageLocationResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

public class PageChooserController implements TransactionalController {

	private PageDao pageDao;

	private PageLocationResolver resolver;

	private String viewName = ResourceUtils.getPath(
			PageChooserController.class, "PageChooserView.ftl");


	public PageChooserController(PageDao pageDao, PageLocationResolver resolver) {
		this.pageDao = pageDao;
		this.resolver = resolver;
	}

	private Locale getLocale(HttpServletRequest request) {
		String localeString = request.getParameter("locale");
		if (localeString != null) {
			return StringUtils.parseLocaleString(localeString);
		}
		List locales = pageDao.getLocales();
		if (locales != null && !locales.isEmpty()) {
			if (locales.size() == 1) {
				return (Locale) locales.get(0);
			}
			Locale locale = RequestContextUtils.getLocale(request);
			if (!locales.contains(locale)) {
				locale = (Locale) locales.get(0);
			}
			return locale;
		}
		return null;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long siteId = ServletRequestUtils.getLongParameter(request, "site");
		Site site = siteId != null
				? pageDao.loadSite(siteId)
				: pageDao.getDefaultSite();

		Locale locale = getLocale(request);

		HashMap model = new HashMap();
		model.put("mode", ServletRequestUtils.getStringParameter(
				request, "mode", "forms"));

		model.put("locales", pageDao.getLocales());
		model.put("selectedLocale", locale);
		model.put("sites", pageDao.listSites());
		model.put("selectedSite", site);
		model.put("pages", createpageLinks(
				pageDao.findRootNode(site).getChildPages(locale)));

		return new ModelAndView(viewName, model);
	}

	private List createpageLinks(Collection pages) {
		ArrayList links = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (!page.isWildcardMapping()) {
				PageLink link = new PageLink();
				link.setPathComponent(page.getPathComponent());
				link.setLink(resolver.getUrl(new PageLocation(page)));
				link.setTitle(page.getProperty("title", true));
				link.setPublished(page.isPublished());
				link.setChildPages(createpageLinks(page.getChildPages()));
				links.add(link);
			}
		}
		return links;
	}
}
