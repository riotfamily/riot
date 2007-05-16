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
package org.riotfamily.pages.mapping;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.controller.HttpErrorController;
import org.riotfamily.common.web.controller.RedirectController;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageAlias;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.riot.security.AccessController;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class PageHandlerMapping extends AbstractHandlerMapping {

	private static final String PAGE_ATTRIBUTE =
			PageHandlerMapping.class.getName() + ".page";

	private static final String WILDCARD_MATCH_ATTRIBUTE =
		PageHandlerMapping.class.getName() + ".wildcardMatch";

	private static final Log log = LogFactory.getLog(PageHandlerMapping.class);

	private PageDao pageDao;

	private PageLocationResolver locationResolver;

	private boolean wildcardsEnabled = true;

	private Object defaultPageHandler;

	public PageHandlerMapping(PageDao pageDao,
			PageLocationResolver pathAndLocaleResolver) {

		this.pageDao = pageDao;
		this.locationResolver = pathAndLocaleResolver;
	}

	public void setDefaultPageHandler(Object defaultPageHandler) {
		this.defaultPageHandler = defaultPageHandler;
	}

	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {

		PageLocation location = locationResolver.getPageLocation(request);
		Page page = pageDao.findPage(location);
		if (page == null && wildcardsEnabled) {
			String path = location.getPath();
			int i = path.lastIndexOf('/');
			PageLocation wildcardLocation = new PageLocation(
					path.substring(0, i) + "/*", location.getLocale());

			page = pageDao.findPage(wildcardLocation);
			if (page != null) {
				String wildcardMatch = path.substring(i + 1);
				request.setAttribute(WILDCARD_MATCH_ATTRIBUTE, wildcardMatch);
			}
		}
		log.debug("Page: " + page);
		if (page != null) {
			if (isRequestable(page)) {
				if (page.isFolder()) {
					String url = getFirstVisibleChildPageUrl(page);
					if (url != null) {
						return new RedirectController(url);
					}
					return new HttpErrorController(HttpServletResponse.SC_NOT_FOUND);
				}
				request.setAttribute(PAGE_ATTRIBUTE, page);
				String handlerName = page.getHandlerName();
				if (handlerName != null) {
					return getApplicationContext().getBean(handlerName);
				}
				return defaultPageHandler;
			}
		}
		else {
			PageAlias alias = pageDao.findPageAlias(location);
			if (alias != null) {
				page = alias.getPage();
				if (page != null) {
					String url = locationResolver.getUrl(
							new PageLocation(page));

					return new RedirectController(url, true, false);
				}
				else {
					return new HttpErrorController(HttpServletResponse.SC_GONE);
				}
			}
		}
		return null;
	}

	private boolean isRequestable(Page page) {
		return page.isPublished() || AccessController.isAuthenticatedUser();
	}

	private String getFirstVisibleChildPageUrl(Page parent) {
		Iterator it = parent.getChildPages().iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (isRequestable(page)) {
				return locationResolver.getUrl(new PageLocation(page));
			}
		}
		return null;
	}

	public static Page getPage(HttpServletRequest request) {
		return (Page) request.getAttribute(PAGE_ATTRIBUTE);
	}

}
