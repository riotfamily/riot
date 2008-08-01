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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.controller.HttpErrorController;
import org.riotfamily.common.web.controller.RedirectController;
import org.riotfamily.common.web.mapping.AbstractReverseHandlerMapping;
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.common.web.mapping.UrlResolverContext;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.Site;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.WebUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class PageHandlerMapping extends AbstractReverseHandlerMapping {

	private PageDao pageDao;

	private PageResolver pageResolver;
	
	private PathCompleter pathCompleter;

	private Object defaultPageHandler;

	private PathMatcher pathMatcher = new AntPathMatcher();
	

	public PageHandlerMapping(PageDao pageDao, PageResolver pageResolver,
			PathCompleter pathCompleter) {

		this.pageDao = pageDao;
		this.pageResolver = pageResolver;
		this.pathCompleter = pathCompleter;
	}

	/**
	 * Sets the handler to use if no matching handler for the page's
	 * pageType is found. The handler will only be returned when the request
	 * maps to a page. Otherwise the {@link #getDefaultHandler() default handler}
	 * will be used.
	 */
	public void setDefaultPageHandler(Object defaultPageHandler) {
		this.defaultPageHandler = defaultPageHandler;
	}

	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {

		if (WebUtils.isIncludeRequest(request)) {
			return null;
		}
		Page page = pageResolver.getPage(request);
		String path = pageResolver.getPathWithinSite(request);
		if (page == null || !page.isRequestable()) {
			Site site = pageResolver.getSite(request);
			if (site == null) {
				return null;
			}
			
			return getPageNotFoundHandler(site, path);
		}
		
		if (!page.isWildcardInPath()) {
			exposePathWithinMapping(path, request);
		}
		else {
			exposeAttributes(page.getPath(), path, request);
			exposePathWithinMapping(pathMatcher.extractPathWithinPattern(
					page.getPath(), path), request);
		}
		
		return getPageHandler(page, request);
	}
	
	
	/**
	 * Returns the handler for the given page.
	 */
	protected Object getPageHandler(Page page, HttpServletRequest request) {
		if (page.isFolder()) {
			return getFolderHandler(page);
		}
		if (page.isRequestable()) {
			String handlerName = page.getPageType();
			if (handlerName != null && getApplicationContext().containsBean(handlerName)) {
				Object handler = getApplicationContext().getBean(handlerName);
				exposeHandlerName(handlerName, request);
				return handler;
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
		for (Page page : folder.getChildPages()) {
			if (page.isRequestable()) {
				String url = page.getUrl(pathCompleter);
				return new RedirectController(url, true, false);
			}
		}
		return null;
	}
	
	/**
	 * Checks if an alias is registered for the given site and path and returns 
	 * a RedirectController, or <code>null</code> in case no alias can be found.
	 */
	protected Object getPageNotFoundHandler(Site site, String path) {
		try {
			PageAlias alias = pageDao.findPageAlias(site, path);
			if (alias != null) {
				Page page = alias.getPage();
				if (page != null) {
					String url = page.getUrl(pathCompleter);
					return new RedirectController(url, true, false);
				}
				else {
					return new HttpErrorController(HttpServletResponse.SC_GONE);
				}
			}
			return null;
		}
		catch (HibernateSystemException e) {
			// No hibernate session bound to thread
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
	
	protected List<AttributePattern> getPatternsForHandler(String beanName, 
			UrlResolverContext context) {
		
		Site site = PageResolver.getResolvedSite(context);
		if (site == null) {
			return null;
		}
		List<Page> pages = pageDao.findPagesOfType(beanName, site);
		ArrayList<AttributePattern> patterns = new ArrayList<AttributePattern>(pages.size());
		for (Page page : pages) {
			patterns.add(new AttributePattern(page.getUrl(pathCompleter)));
		}
		return patterns;
	}

}
