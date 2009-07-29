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
package org.riotfamily.pages.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.pages.model.Site;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class SiteListController extends AbstractCacheableController {

	private String viewName;

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
					HttpServletResponse response) throws Exception {

		List<Site> sites = Site.findAll();
		if (sites.size() == 1) {
			Site site = sites.get(0);
			String url = site.getRootPage().getUrl();
			return new ModelAndView(new RedirectView(url, true));
		}
		if (!sites.isEmpty()) {
			CachiusContext.tag(Site.class.getName());
			return new ModelAndView(viewName, "sites", sites);
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}

	public long getTimeToLive(HttpServletRequest request) {
		return CacheableController.CACHE_ETERNALLY;
	}

}
