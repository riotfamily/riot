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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.controller.HttpErrorController;
import org.riotfamily.common.web.controller.RedirectController;
import org.riotfamily.common.web.mapping.AbstractReverseHandlerMapping;
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageAlias;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.riot.security.AccessController;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class PageHandlerMapping extends AbstractReverseHandlerMapping {

	private static final String PAGE_ATTRIBUTE =
			PageHandlerMapping.class.getName() + ".page";

	private static final Log log = LogFactory.getLog(PageHandlerMapping.class);

	private PathMatcher pathMatcher = new AntPathMatcher();
	
	private PageDao pageDao;

	private PageLocationResolver locationResolver;

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
		String urlPath = location.getPath();
		if (location == null) {
			return null;
		}
		Page page = pageDao.findPage(location);
		if (page == null) {
			String bestMatch = null;
			for (Iterator it = pageDao.getWildcardPaths(location).iterator(); it.hasNext();) {
				String path = (String) it.next();
				String antPattern = AttributePattern.convertToAntPattern(path);
				if (pathMatcher.match(antPattern, urlPath) &&
						(bestMatch == null || bestMatch.length() <= path.length())) {

					bestMatch = path;
				}
			}
			if (bestMatch != null) {
				location.setPath(bestMatch);
				page = pageDao.findPage(location);
				exposeAttributes(bestMatch, urlPath, request);
				exposePathWithinMapping(pathMatcher.extractPathWithinPattern(bestMatch, urlPath), request);
			}
		}
		else {
			exposePathWithinMapping(urlPath, request);
		}
		
		log.debug("Page: " + page);
		if (page != null) {
			if (isRequestable(page)) {
				request.setAttribute(PAGE_ATTRIBUTE, page);
				String handlerName = page.getHandlerName();
				if (handlerName != null) {
					exposeHandlerName(handlerName, request);
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
					String url = locationResolver.getUrl(page);

					return new RedirectController(url, true, false);
				}
				else {
					return new HttpErrorController(HttpServletResponse.SC_GONE);
				}
			}
		}
		return null;
	}
	
	/**
	 * <strong>Copied from AbstractUrlHandlerMapping</strong>
	 */
	protected void exposePathWithinMapping(String pathWithinMapping, HttpServletRequest request) {
		request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, pathWithinMapping);
	}
	
	protected void exposeAttributes(String antPattern, String urlPath,
			HttpServletRequest request) {

		AttributePattern pattern = new AttributePattern(antPattern);
		pattern.expose(urlPath, request);
	}
	
	public static Map getWildcardAttributes(HttpServletRequest request) {
		return (Map) request.getAttribute(AttributePattern.EXPOSED_ATTRIBUTES);
	}
	
	private boolean isRequestable(Page page) {
		return page.isEnabled() || AccessController.isAuthenticatedUser();
	}

	public static Page getPage(HttpServletRequest request) {
		return (Page) request.getAttribute(PAGE_ATTRIBUTE);
	}
	

	protected List getPatternsForHandler(String beanName, 
			HttpServletRequest request) {
		
		Locale locale = RequestContextUtils.getLocale(request);
		List pages = pageDao.findPagesForHandler(beanName, locale);
		ArrayList patterns = new ArrayList(pages.size());
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			patterns.add(new AttributePattern(locationResolver.getUrl(page)));
		}
		return patterns;
	}

}
