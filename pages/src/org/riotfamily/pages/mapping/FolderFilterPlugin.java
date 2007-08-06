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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.filter.FilterPlugin;
import org.riotfamily.common.web.filter.PluginChain;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.riot.security.AccessController;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class FolderFilterPlugin extends FilterPlugin {

	private static final TransactionDefinition TX_DEF = 
			new DefaultTransactionDefinition();
	
	private PageDao pageDao;
	
	private PageLocationResolver locationResolver;
	
	private PlatformTransactionManager tx;
	
	
	public FolderFilterPlugin(PageDao pageDao, 
			PageLocationResolver locationResolver, 
			PlatformTransactionManager tx) {
		
		this.pageDao = pageDao;
		this.locationResolver = locationResolver;
		this.tx = tx;
	}

	public void doFilter(HttpServletRequest request, 
			HttpServletResponse response, PluginChain pluginChain)
			throws IOException, ServletException {
		
		boolean requestHandled = false;
		String uri = request.getRequestURI();
		if (uri.lastIndexOf('.') < uri.lastIndexOf('/')) {
			TransactionStatus status = tx.getTransaction(TX_DEF);
			try {
				Collection indexPages = getIndexPages(request);
				if (indexPages != null) {
					requestHandled = true;
					sendRedirect(indexPages, request, response);
				}
			}
			catch (Exception ex) {
				tx.rollback(status);
			    throw new ServletException(ex);
			}
			tx.commit(status);
		}
		if (!requestHandled) {
			pluginChain.doFilter(request, response);
		}
	}

	private boolean isRequestable(Page page) {
		return page.isEnabled() || AccessController.isAuthenticatedUser();
	}
	
	private Collection getIndexPages(HttpServletRequest request) {
		PageLocation location = locationResolver.getPageLocation(request);
		Collection childPages = null;
		if (location.getPath().equals("/")) {
			Site site = pageDao.getSite(location.getSiteName());
			childPages = pageDao.findRootNode(site).getChildPages(location.getLocale());
		}
		else {
			Page page = pageDao.findPage(location);
			if (page != null && page.isFolder()) {
				childPages = page.getChildPages();
			}
		}
		return childPages;
	}
	
	private void sendRedirect(Collection pages, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		String url = getFirstVisibleChildPageUrl(pages);
		if (url != null) {
			response.sendRedirect(response.encodeRedirectURL(
					request.getContextPath() + url));
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	private String getFirstVisibleChildPageUrl(Collection pages) {
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (isRequestable(page)) {
				return locationResolver.getUrl(page);
			}
		}
		return null;
	}
}
