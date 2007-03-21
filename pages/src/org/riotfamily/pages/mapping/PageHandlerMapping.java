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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class PageHandlerMapping extends AbstractHandlerMapping {

	private static final Log log = LogFactory.getLog(PageHandlerMapping.class);
	
	private PageDao pageDao;
	
	private PlatformTransactionManager transactionManager;
	
	private PageLocationResolver locationResolver;
	
	private Object defaultPageHandler;
	
	public PageHandlerMapping(PageDao pageDao, 
			PlatformTransactionManager transactionManager,
			PageLocationResolver pathAndLocaleResolver) {

		this.pageDao = pageDao;
		this.transactionManager = transactionManager;
		this.locationResolver = pathAndLocaleResolver;
	}

	public void setDefaultPageHandler(Object defaultPageHandler) {
		this.defaultPageHandler = defaultPageHandler;
	}
	
	protected Object getHandlerInternal(final HttpServletRequest request) 
			throws Exception {

		return new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				return getHandlerWithinTransaction(request);
			}
		});
	}
	
	protected Object getHandlerWithinTransaction(HttpServletRequest request) {
		PageLocation location = locationResolver.getPageLocation(request); 
		Page page = pageDao.findPage(location);
		log.debug("Page: " + page);
		if (page != null) {
			request.setAttribute("page", page);
			String handlerName = page.getHandlerName();
			if (handlerName != null) {
				return getApplicationContext().getBean(handlerName);
			}
			return defaultPageHandler;
		}
		else {
			PageAlias alias = pageDao.findPageAlias(location);
			if (alias != null) {
				page = alias.getPage();
				if (page != null) {
					String url = locationResolver.getUrl(
							new PageLocation(page), request);
					
					return new RedirectController(url);
				}
				else {
					return new HttpErrorController(HttpServletResponse.SC_GONE);
				}
			}
		}
		return null;
	}
	
}
