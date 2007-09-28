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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageUrlBuilder;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class LocaleListController extends AbstractCacheableController {

	private PageDao pageDao;

	private PageUrlBuilder pageUrlBuilder;

	private String siteName;

	private String viewName;

	public LocaleListController(PageDao pageDao,
			PageUrlBuilder pageUrlBuilder) {

		this.pageDao = pageDao;
		this.pageUrlBuilder = pageUrlBuilder;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
					HttpServletResponse response) throws Exception {

		/*
		Site site = null;
		if (siteName != null) {
			site  = pageDao.getSite(siteName);
		}
		else {
			site = pageDao.getDefaultSite();
		}

		if (site != null) {
			List locales = null;
			if (AccessController.isAuthenticatedUser()) {
				locales = pageDao.getLocales();
			}
			else if (site.isEnabled()) {
				locales = site.getLocales();
			}
			if (locales != null) {
				if (locales.size() == 1) {
					Locale locale = (Locale)locales.get(0);
					PageNode root = pageDao.findRootNode(site);
					Page page = (Page) root.getChildPages(locale).iterator().next();
					String url = locationResolver.getUrl(page);
					url = ServletUtils.resolveUrl(url, request);
					return new ModelAndView(new RedirectView(url));
				}
				if (!locales.isEmpty()) {
					return new ModelAndView(viewName, "locales", locales);
				}
			}
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		*/
		return null;
	}

	public long getTimeToLive(HttpServletRequest request) {
		return CacheableController.CACHE_ETERNALLY;
	}

}
