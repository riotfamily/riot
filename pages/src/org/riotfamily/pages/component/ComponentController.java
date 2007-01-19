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
package org.riotfamily.pages.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.cachius.spring.TaggingContext;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ComponentController extends AbstractCacheableController {

	private ComponentDao componentDao;
	
	private ComponentRepository componentRepository;
	
	private ViewModeResolver viewModeResolver;
	
	private String requiredType;
	
	private String viewName;
	
	
	public ComponentDao getComponentDao() {
		return this.componentDao;
	}

	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}

	public ComponentRepository getComponentRepository() {
		return this.componentRepository;
	}

	public void setComponentRepository(ComponentRepository componentRepository) {
		this.componentRepository = componentRepository;
	}

	public ViewModeResolver getViewModeResolver() {
		return this.viewModeResolver;
	}

	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	public void setRequiredType(String requiredType) {
		this.requiredType = requiredType;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	protected Long getVersionId(HttpServletRequest request) {
		String s = request.getParameter("id");
		return s != null? new Long(s) : null;
	}
	
	protected void appendCacheKey(StringBuffer key, 
			HttpServletRequest request) {
		
		super.appendCacheKey(key, request);
		key.append("?id=").append(getVersionId(request));
	}
	
	protected boolean bypassCache(HttpServletRequest request) {
		return viewModeResolver.isPreviewMode(request);
	}
	
	public long getTimeToLive(HttpServletRequest request) {
		return -1;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Long id = getVersionId(request);
		ComponentVersion version = componentDao.loadComponentVersion(id);
		Assert.notNull(version, "No such component: " + id);

		ComponentList list = version.getContainer().getList();
		String listTag = list.getPath() + ':' + list.getKey();
		TaggingContext.tag(request, listTag);
		
		Component component = componentRepository.getComponent(version);
		
		Assert.isTrue(requiredType == null || 
				version.getType().equals(requiredType), 
				"Component must be of type " + requiredType);
		
		return new ModelAndView(viewName, component.buildModel(version));
	}
	
}
