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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *   Carsten Woelk [cwoelk at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.mapping;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.common.web.mapping.UrlResolverContext;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.Site;
import org.springframework.util.StringUtils;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PageResolver {
	
	private RiotLog log = RiotLog.get(PageResolver.class);

	public static final String PATH_ATTRIBUTE = PageResolver.class.getName() + ".path";

	public static final String SITE_ATTRIBUTE = PageResolver.class.getName() + ".site";

	public static final String PAGE_ATTRIBUTE = PageResolver.class.getName() + ".page";

	private static final Object NOT_FOUND = new Object();
	
	private PageDao pageDao;
	
	private PathCompleter pathCompleter;

	public PageResolver(PageDao pageDao, PathCompleter pathCompleter) {
		this.pageDao = pageDao;
		this.pathCompleter = pathCompleter;
	}
	
	/**
	 * Returns the first Site that matches the given request. The PathCompleter
	 * is used to strip the servlet mapping from the request URI.
	 * @return The first matching Site, or <code>null</code> if no match is found
	 */
	public Site getSite(HttpServletRequest request) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		if (site == null) {
			site = resolveSite(request);
			expose(site, request, SITE_ATTRIBUTE);
		}
		return site != NOT_FOUND ? (Site) site : null; 
	}
		
	/**
	 * Returns the path within the resolved Site.
	 */
	public String getPathWithinSite(HttpServletRequest request) {
		Object path = request.getAttribute(PATH_ATTRIBUTE);
		if (path == null) {
			// This will exposes the path attribute as side effect:
			Site site = getSite(request);
			if (site != null) {
				path = request.getAttribute(PATH_ATTRIBUTE);
			}
		}
		return path != NOT_FOUND ? (String) path : null;
	}
	
	/**
	 * Returns the Page for the given request.
	 */
	public Page getPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		if (page == null) {
			page = resolvePage(request);
			expose(page, request, PAGE_ATTRIBUTE);
		}
		return page != NOT_FOUND ? (Page) page : null;
	}
	
	/**
	 * Returns the previously resolved Page for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getPage(HttpServletRequest)} has been invoked before. 
	 */
	public static Page getResolvedPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		return page != NOT_FOUND ? (Page) page : null;
	}
	
	/**
	 * Returns the previously resolved Page for the given context.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getPage(HttpServletRequest)} has been invoked before. 
	 */
	public static Page getResolvedPage(UrlResolverContext context) {
		Object page = context.getAttribute(PAGE_ATTRIBUTE);
		return page != NOT_FOUND ? (Page) page : null;
	}
	
	/**
	 * Returns the previously resolved Site for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getSite(HttpServletRequest)} has been invoked before. 
	 */
	public static Site getResolvedSite(HttpServletRequest request) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		return site != NOT_FOUND ? (Site) site : null; 
	}
	
	/**
	 * Returns the previously resolved Site for the given context.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getSite(HttpServletRequest)} has been invoked before. 
	 */
	public static Site getResolvedSite(UrlResolverContext context) {
		Object site = context.getAttribute(SITE_ATTRIBUTE);
		return site != NOT_FOUND ? (Site) site : null; 
	}
	
	/**
	 * Returns the previously resolved Path within the Site for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getSite(HttpServletRequest)} has been invoked before. 
	 */
	public static String getResolvedPathWithinSite(HttpServletRequest request) {
		return (String)request.getAttribute(PATH_ATTRIBUTE);
	}

	/**
	 * Returns the previously resolved Path within the Site for the given context.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getSite(HttpServletRequest)} has been invoked before. 
	 */
	public static String getResolvedPathWithinSite(UrlResolverContext context) {
		return (String)context.getAttribute(PATH_ATTRIBUTE);
	}

	/**
	 * Returns the Page which is requestable at the given URL. This may return
	 * <code>null</code> in case the given parameters do not match a page.
	 * 
	 * @param url url  of the requestable page
	 * @param contextPath of the application in order to strip it
	 * @param fallbackSite in case the site can't be looked up, this site will
	 * 			be used to find the page
	 * @param pathCompleter in order to strip the servlet mapping
	 * @return the page matching the parameters or null if no page was found
	 */
	public Page resolvePage(String url, String contextPath, Site fallbackSite) {
		
		String host = ServletUtils.getHost(url);
		String path = ServletUtils.getPath(url);

		// Strip the contextPath if known
		if (StringUtils.startsWithIgnoreCase(path, contextPath)) {
			path = path.substring(contextPath.length());
		}
		log.debug("Path is '" + path + "'.");
		
		if (path == null) {
			log.warn("The path is null. Can't continue.");
			return null;
		}

		path = pathCompleter.stripMapping(path);
		log.debug("Path is now '" + path + "'.");
		log.debug("Host is '" + host + "'.");

		Site site = pageDao.findSite(host, path);
		if (site == null) {
			log.warn("Could not find site for url '" + url + "'. Using fallback.");
			site = fallbackSite;
		}
		path = site.stripPrefix(path);
		log.debug("Path is now '" + path + "'.");

		Page page = pageDao.findPage(site, path);
		if (page == null) {
			log.debug("Haven't found a page for '" + site + path + "'. Trying to find a page through an alias.");
			PageAlias alias = pageDao.findPageAlias(site, path);
			if (alias != null) {
				page = alias.getPage();
			}
		}
		
		log.debug("Page: " + page);

		return page;
	}	

	
	private Site resolveSite(HttpServletRequest request) {
		String hostName = request.getServerName();
		String path = ServletUtils.getPathWithinApplication(request);
		path = pathCompleter.stripMapping(path);
		Site site = pageDao.findSite(hostName, path);
		String pathWithinSite = null;
		if (site != null) {
			pathWithinSite = FormatUtils.stripTrailingSlash(site.stripPrefix(path));
		}
		exposePathWithinSite(pathWithinSite, request);
		// REVISIT: Maybe check if site is visible, like in #resolvePage
		return site;
	}

	protected void exposePathWithinSite(String pathWithinSite, HttpServletRequest request) {
		expose(pathWithinSite, request, PATH_ATTRIBUTE);
	}

	private Page resolvePage(HttpServletRequest request) {
		Site site = getSite(request);
		if (site == null) {
			return null;
		}
		String path = getPathWithinSite(request);
		Page page = pageDao.findPage(site, path);
		if (page == null) {
			page = findWildcardPage(site, path);
		}
		if (page == null || !page.isRequestable()) {
			return null;
		}
		return page;
	}
	
	private Page findWildcardPage(Site site, String urlPath) {
		Page page = null; 
		AttributePattern bestMatch = null;
		for (String path : pageDao.getWildcardPaths(site)) {
			AttributePattern p = new AttributePattern(path);
			if (p.matches(urlPath) && p.isMoreSpecific(bestMatch)) {
				bestMatch = p;
			}
		}
		if (bestMatch != null) {
			page = pageDao.findPage(site, bestMatch.toString());
		}
		return page;
	}

	private void expose(Object object, HttpServletRequest request,
			String attributeName) {
		
		if (object == null) {
			object = NOT_FOUND;
			log.debug("Exposing 'NOT_FOUND' as '" + attributeName + "'");
		}
		else {
			log.debug("Exposing '" + object + "' as '" + attributeName + "'");
		}
		request.setAttribute(attributeName, object);
	}
	
	/**
	 * Resets all internally used attributes.
	 * @param request
	 */
	public static void resetAttributes(HttpServletRequest request) {
		request.removeAttribute(SITE_ATTRIBUTE);
		request.removeAttribute(PAGE_ATTRIBUTE);
		request.removeAttribute(PATH_ATTRIBUTE);
	}
}
