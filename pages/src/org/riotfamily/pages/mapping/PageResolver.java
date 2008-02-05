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
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 7.0
 */
public class PageResolver {
	
	private static final Log log = LogFactory.getLog(PageResolver.class);

	private static final String PATH_ATTRIBUTE = PageResolver.class.getName() + ".path";

	private static final String SITE_ATTRIBUTE = PageResolver.class.getName() + ".site";

	private static final String PAGE_ATTRIBUTE = PageResolver.class.getName() + ".page";

	private static final Object NOT_FOUND = new Object();
	
	private PageDao pageDao;

	private PathMatcher pathMatcher = new AntPathMatcher();
	

	public PageResolver(PageDao pageDao) {
		this.pageDao = pageDao;
	}


	private Site resolveSite(HttpServletRequest request, PathCompleter pathCompleter) {
		String hostName = request.getServerName();
		String path;
		if (pathCompleter == null) {
			path = ServletUtils.getOriginatingPathWithoutServletMapping(request);
			log.debug("Path without a PathCompleter resolved to '" + path + "'.");
		}
		else {
			path = ServletUtils.getOriginatingPathWithinApplication(request);
			path = pathCompleter.stripServletMapping(path);
			log.debug("Path with a PathCompleter resolved to '" + path + "'.");
		}
		Site site = pageDao.findSite(hostName, path);
		exposePathWithingSite(request, path, site);
		return site;
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

	private void exposePathWithingSite(HttpServletRequest request, String path,
			Site site) {

		if (site == null) {
			path = null;
		}
		else {
			path = site.stripPrefix(path);
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
		}
		expose(path, request, PATH_ATTRIBUTE);
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
	
	

	public String getPathWithinSite(HttpServletRequest request) {
		return getPathWithinSite(request, null);
	}

	
	// For Filters where we can't know about the ServletMapping and the ServletPath yet...
	public String getPathWithinSite(HttpServletRequest request, PathCompleter pathCompleter) {
		Object path = request.getAttribute(PATH_ATTRIBUTE);
		if (path == null) {
			// The resolveSite exposes the path Attribute
			Site site = getSite(request, pathCompleter);
			if (site != null) {
				path = request.getAttribute(PATH_ATTRIBUTE);
			}
		}
		return path != NOT_FOUND ? (String) path : null;
	}


	public Site getSite(HttpServletRequest request) {
		return getSite(request, null);
	}

	
	// For Filters where we can't know about the ServletMapping and the ServletPath yet...
	public Site getSite(HttpServletRequest request, PathCompleter pathCompleter) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		if (site == null) {
			site = resolveSite(request, pathCompleter);
			expose(site, request, SITE_ATTRIBUTE);
		}
		return site != NOT_FOUND ? (Site) site : null; 
	}

	
	public Page getPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		if (page == null) {
			page = resolvePage(request);
			expose(page, request, PAGE_ATTRIBUTE);
		}
		return page != NOT_FOUND ? (Page) page : null;
	}
	
	
	public static Page getResolvedPage(HttpServletRequest request) {
		return (Page) request.getAttribute(PAGE_ATTRIBUTE);
	}
	
	
}
