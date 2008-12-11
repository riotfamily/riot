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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.controller.HttpErrorController;
import org.riotfamily.common.web.controller.RedirectController;
import org.riotfamily.common.web.mapping.AbstractReverseHandlerMapping;
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.Site;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerMapping;

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

	private PageUrlBuilder pageUrlBuilder;

	private Object defaultPageHandler;

	public PageHandlerMapping(PageDao pageDao,
			PageUrlBuilder pageUrlBuilder) {

		this.pageDao = pageDao;
		this.pageUrlBuilder = pageUrlBuilder;
	}

	public void setDefaultPageHandler(Object defaultPageHandler) {
		this.defaultPageHandler = defaultPageHandler;
	}

	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {

		String hostName = request.getServerName();
		String path = ServletUtils.getPathWithoutServletMapping(request);
		Site site = pageDao.findSite(hostName, path);
		if (site == null) {
			return null;
		}
		path = site.stripPrefix(path);
		Page page = pageDao.findPage(site, path);
		if (page != null) {
			exposePathWithinMapping(path, request);
		}
		else {
			page = findWildcardPage(site, path);
			if (page != null) {
				exposeAttributes(page, path, request);
				exposePathWithinMapping(pathMatcher.extractPathWithinPattern(
						page.getPath(), path), request);
			}
		}
		
		log.debug("Page: " + page);
		if (page != null) {
			return getPageHandler(page, request);
		}
		return getPageNotFoundHandler(site, path);
	}
	
	protected Page findWildcardPage(Site site, String urlPath) {
		Page page = null; 
		String bestMatch = null;
		for (Iterator it = pageDao.getWildcardPaths(site).iterator(); it.hasNext();) {
			String path = (String) it.next();
			String antPattern = AttributePattern.convertToAntPattern(path);
			if (pathMatcher.match(antPattern, urlPath) &&
					(bestMatch == null || bestMatch.length() <= path.length())) {

				bestMatch = path;
			}
		}
		if (bestMatch != null) {
			page = pageDao.findPage(site, bestMatch);
		}
		return page;
	}
	
	/**
	 * Returns the handler for the given page.
	 */
	protected Object getPageHandler(Page page, HttpServletRequest request) {
		if (page.isFolder()) {
			return getFolderHandler(page);
		}
		if (page.isRequestable()) {
			request.setAttribute(PAGE_ATTRIBUTE, page);
			String handlerName = page.getHandlerName();
			if (handlerName != null) {
				exposeHandlerName(handlerName, request);
				return getApplicationContext().getBean(handlerName);
			}
			return defaultPageHandler;
		}
		return null;
	}

	/**
	 * Returns a Controller that sends a redirect to the request to the first 
	 * requestable child page.
	 */
	private Object getFolderHandler(Page folder) {
		Iterator it = folder.getChildPages().iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (page.isRequestable()) {
				String url = pageUrlBuilder.getUrl(page);
				return new RedirectController(url, true, false, true);
			}
		}
		return null;
	}
	
	/**
	 * Checks if an alias is registered for the given site and path and returns 
	 * a RedirectController, or <code>null</code> in case no alias can be found.
	 */
	protected Object getPageNotFoundHandler(Site site, String path) {
		PageAlias alias = pageDao.findPageAlias(site, path);
		if (alias != null) {
			Page page = alias.getPage();
			if (page != null) {
				String url = pageUrlBuilder.getUrl(page);
				return new RedirectController(url, true, false);
			}
			else {
				return new HttpErrorController(HttpServletResponse.SC_GONE);
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
	
	protected void exposeAttributes(Page page, String urlPath,
			HttpServletRequest request) {

		AttributePattern pattern = new AttributePattern(page.getPath());
		pattern.expose(urlPath, request);
	}
	
	public static Map getWildcardAttributes(HttpServletRequest request) {
		return (Map) request.getAttribute(AttributePattern.EXPOSED_ATTRIBUTES);
	}
	
	public static Page getPage(HttpServletRequest request) {
		return (Page) request.getAttribute(PAGE_ATTRIBUTE);
	}
	

	protected List getPatternsForHandler(String beanName, 
			HttpServletRequest request) {
		
		Page currentPage = getPage(request);
		Assert.notNull(currentPage, "This method can only be used on pages " +
				"whose handler was resolved by a PageHandlerMapping.");
		
		Site site = currentPage.getSite();
		List pages = pageDao.findPagesForHandler(beanName, site);
		ArrayList patterns = new ArrayList(pages.size());
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			patterns.add(new AttributePattern(pageUrlBuilder.getUrl(page)));
		}
		return patterns;
	}

}
