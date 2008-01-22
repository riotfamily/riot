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
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.Site;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 7.0
 */
public class PagesMacroHelper {

	private PageDao pageDao;
	
	private PathCompleter pathCompleter;

	private static final Log log = LogFactory.getLog(PagesMacroHelper.class);
	
	public PagesMacroHelper(PageDao pageDao, PathCompleter pathCompleter) {
		this.pageDao = pageDao;
		this.pathCompleter = pathCompleter;
	}
	
	public Page pageForUrl(String url, Site fallbackSite) {

		String host = ServletUtils.getHost(url);
		String path = ServletUtils.getPath(url);
		log.debug("Host is '" + host + "'.");
		log.debug("Path is '" + path + "'.");
		
		if (path == null) {
			log.warn("The path is null. Can't continue.");
			return null;
		}
		
		path = pathCompleter.stripServletMapping(path);
		log.debug("Path is now '" + path + "'.");
		
		Site site = pageDao.findSite(host, path);
		if (site == null) {
			log.warn("Could not find site for url '" + url + "'. Using fallback.");
			site = fallbackSite;
		}
		path = stripSitePrefix(site, path);
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

	
	public String stripSitePrefix(Site site, String path) {
		String pathPrefix = site.getPathPrefix();
		if (pathPrefix != null && path.startsWith(pathPrefix)) {
			log.debug("Stripping pathPrefix '" + pathPrefix + "'.");
			return path.substring(pathPrefix.length());
		}
		return path;
	}


}
