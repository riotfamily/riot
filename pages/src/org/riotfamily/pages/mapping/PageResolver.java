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
 *   Carsten Woelk [cwoelk at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.mapping;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.mapping.AttributePattern;
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

	private PageDao pageDao;

	private PathMatcher pathMatcher = new AntPathMatcher();
	

	public PageResolver(PageDao pageDao) {
		this.pageDao = pageDao;
	}


	private Site resolveSite(HttpServletRequest request) {
		String hostName = request.getServerName();
		String path = ServletUtils.getOriginatingPathWithoutServletMapping(request);
		return pageDao.findSite(hostName, path);
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


	public String getPathWithinSite(HttpServletRequest request) {
		String path = (String) request.getAttribute(PATH_ATTRIBUTE);
		if (path == null) {
			Site site = getSite(request);
			if (site != null) {
				// REVISIT: Müssen wir das wirklich hier nochmal machen?
				path = ServletUtils.getOriginatingPathWithoutServletMapping(request);
				path = site.stripPrefix(path);
				if (path.endsWith("/")) {
					path = path.substring(0, path.length() - 1);
				}
				log.debug("Resolved Path: " + path);
				request.setAttribute(PATH_ATTRIBUTE, path);
			}
		}
		return path;
	}
	
	public Site getSite(HttpServletRequest request) {
		Site site = (Site) request.getAttribute(SITE_ATTRIBUTE);
		if (site == null) {
			site = resolveSite(request);
			log.debug("Resolved Site: " + site);
			request.setAttribute(SITE_ATTRIBUTE, site);
		}
		return site; 
	}

	public Page getPage(HttpServletRequest request) {
		Page page = (Page) request.getAttribute(PAGE_ATTRIBUTE);
		if (page == null) {
			page = resolvePage(request);
			log.debug("Reolved Page: " + page);
			request.setAttribute(PAGE_ATTRIBUTE, page);
		}
		return page;
	}
	
	public static Page getResolvedPage(HttpServletRequest request) {
		return (Page) request.getAttribute(PAGE_ATTRIBUTE);
	}
	
}
