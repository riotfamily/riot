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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	private static final Log log = LogFactory.getLog(PageResolver.class);

	public static final String PATH_ATTRIBUTE = PageResolver.class.getName() + ".path";

	public static final String SITE_ATTRIBUTE = PageResolver.class.getName() + ".site";

	public static final String PAGE_ATTRIBUTE = PageResolver.class.getName() + ".page";

	private static final Object NOT_FOUND = new Object();
	
	private PageDao pageDao;

	public PageResolver(PageDao pageDao) {
		this.pageDao = pageDao;
	}
	
	/**
	 * Returns the first Site that matches the given request.
	 * <p>
	 * <strong>Important:</strong> This method can only be used <em>after</em> 
	 * the servlet container has selected a servlet to handle the the request. 
	 * In other words this method must not be used within a servlet filter 
	 * because it's impossible to determine the servlet mapping at this point.
	 * If you need to resolve the Site in a filter use 
	 * {@link #getSite(HttpServletRequest, PathCompleter) this method} instead.
	 * @return The first matching Site, or <code>null</code> if no match is found
	 */
	public Site getSite(HttpServletRequest request) {
		return getSite(request, null);
	}

	/**
	 * Returns the first Site that matches the given request. The PathCompleter
	 * is used to strip the servlet mapping from the request URI.
	 * <p>
	 * This method is intended for use inside a servlet filter, because in
	 * that phase of request processing the servlet mapping is not known yet.
	 * @return The first matching Site, or <code>null</code> if no match is found
	 */
	public Site getSite(HttpServletRequest request, PathCompleter pathCompleter) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		if (site == null) {
			site = resolveSite(request, pathCompleter);
			expose(site, request, SITE_ATTRIBUTE);
		}
		return site != NOT_FOUND ? (Site) site : null; 
	}
		
	/**
	 * Returns the path within the resolved Site. That is the requestURI 
	 * without the contextPath, without the servlet prefix or suffix and 
	 * without the Site's {@link Site#getPathPrefix() pathPrefix}.
	 * <p>
	 * <strong>Important:</strong> This method can only be used <em>after</em> 
	 * the servlet container has selected a servlet to handle the the request
	 * <em>or</em> {@link #getSite(HttpServletRequest, PathCompleter)} has been
	 * invoked before.
	 * In other words this method should not be used within a servlet filter 
	 * unless you can assert the above. If you are unsure use 
	 * {@link #getPathWithinSite(HttpServletRequest, PathCompleter)} instead.
	 */
	public String getPathWithinSite(HttpServletRequest request) {
		return getPathWithinSite(request, null);
	}

	/**
	 * Returns the path within the resolved Site.
	 * <p>
	 * This method is intended for use inside a servlet filter, because in
	 * that phase of request processing the servlet mapping is not known yet.
	 * @see #getPathWithinSite(HttpServletRequest)
	 */
	public String getPathWithinSite(HttpServletRequest request, PathCompleter pathCompleter) {
		Object path = request.getAttribute(PATH_ATTRIBUTE);
		if (path == null) {
			// This will exposes the path attribute as side effect:
			Site site = getSite(request, pathCompleter);
			if (site != null) {
				path = request.getAttribute(PATH_ATTRIBUTE);
			}
		}
		return path != NOT_FOUND ? (String) path : null;
	}
	
	/**
	 * Returns the Page for the given request.
	 * <p>
	 * <strong>Important:</strong> This method can only be used <em>after</em> 
	 * the servlet container has selected a servlet to handle the the request
	 * <em>or</em> {@link #getSite(HttpServletRequest, PathCompleter)} has been
	 * invoked before.
	 * In other words this method should not be used within a servlet filter 
	 * unless you can assert the above.
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
	 * Returns the previously resolved Site for the given UrlResolverContext.
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
	 * Returns the previously resolved path for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getPathWithinSite(HttpServletRequest)} or
	 * {@link #getPathWithinSite(HttpServletRequest)} has been invoked before.
	 */
	public static String getResolvedPath(HttpServletRequest request) {
		Object path = request.getAttribute(PATH_ATTRIBUTE);
		return path != NOT_FOUND ? (String) path : null;
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
	public Page resolvePage(String url, String contextPath, Site fallbackSite,
			PathCompleter pathCompleter) {
		
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

		path = pathCompleter.stripServletMapping(path);
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

	
	private Site resolveSite(HttpServletRequest request, PathCompleter pathCompleter) {
		String hostName = request.getServerName();
		String path;
		if (pathCompleter == null) {
			path = ServletUtils.getPathWithoutServletMapping(request);
			log.debug("Path without a PathCompleter resolved to '" + path + "'.");
		}
		else {
			path = ServletUtils.getPathWithinApplication(request);
			path = pathCompleter.stripServletMapping(path);
			log.debug("Path with a PathCompleter resolved to '" + path + "'.");
		}
		Site site = pageDao.findSite(hostName, path);
		String pathWithinSite = null;
		if (site != null) {
			pathWithinSite = FormatUtils.stripTrailingSlash(site.stripPrefix(path));
		}
		exposePathWithinSite(pathWithinSite, request);
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
		return page;
	}
	
	private Page findWildcardPage(Site site, String urlPath) {
		Page page = null; 
		AttributePattern bestMatch = null;
		for (Iterator it = pageDao.getWildcardPaths(site).iterator(); it.hasNext();) {
			String path = (String) it.next();
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
	
	
}
