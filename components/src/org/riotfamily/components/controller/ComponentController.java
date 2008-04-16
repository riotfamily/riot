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
package org.riotfamily.components.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.Content;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that exposes the properties of the requested
 * {@link Content} to the model.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ComponentController extends AbstractCacheableController {

	private PlatformTransactionManager transactionManager;

	private ComponentDao componentDao;

	private String viewName;

	private String contentType;
	

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public ComponentDao getComponentDao() {
		return this.componentDao;
	}

	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	

	protected Long getComponentId(HttpServletRequest request) {
		String s = request.getParameter("id");
		return s != null? new Long(s) : null;
	}

	protected void appendCacheKey(StringBuffer key,
			HttpServletRequest request) {

		super.appendCacheKey(key, request);
		key.append("?id=").append(getComponentId(request));
	}

	public long getTimeToLive(HttpServletRequest request) {
		return CACHE_ETERNALLY;
	}

	public ModelAndView handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		return (ModelAndView) new TransactionTemplate(transactionManager)
				.execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus status) {
				return handleRequestInTransaction(request, response);
			}
		});
	}

	protected ModelAndView handleRequestInTransaction(
			HttpServletRequest request, HttpServletResponse response) {

		Long id = getComponentId(request);
		Component component = (Component)componentDao.loadContentContainer(id);
		Assert.notNull(component, "No such Component: " + id);
		
		boolean preview = EditModeUtils.isEditMode(request);
		ComponentCacheUtils.addContainerTags(request, component, preview);
		
		if (contentType != null) {
			response.setContentType(contentType);
		}
		
		return new ModelAndView(viewName, component.unwrapValues(preview));
	}

}
