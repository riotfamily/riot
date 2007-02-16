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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.Cache;
import org.riotfamily.pages.component.config.ComponentListConfiguration;
import org.riotfamily.pages.component.context.PageRequestUtils;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.component.render.EditModeRenderStrategy;
import org.riotfamily.pages.component.render.LiveModeRenderStrategy;
import org.riotfamily.pages.component.render.RenderStrategy;
import org.riotfamily.pages.component.resolver.ComponentKeyResolver;
import org.riotfamily.pages.component.resolver.ComponentPathResolver;
import org.riotfamily.pages.component.resolver.FixedComponentKeyResolver;
import org.riotfamily.pages.component.resolver.FixedComponentPathResolver;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that renders a ComponentList. Which list is to be rendered is 
 * determined using a {@link ComponentPathResolver} and a 
 * {@link ComponentKeyResolver}. 
 */
public class ComponentListController implements Controller, BeanNameAware,
		ComponentListConfiguration {	
	
	private Cache cache;

	private ComponentDao componentDao;

	private ComponentKeyResolver componentKeyResolver;

	private ComponentPathResolver componentPathResolver;

	private String[] initialComponentTypes;

	private Integer minComponents;
	
	private Integer maxComponents;

	private ComponentRepository componentRepository;

	private String[] validComponentTypes;

	private ViewModeResolver viewModeResolver;

	private String beanName;
	
	private PlatformTransactionManager transactionManager;
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public Cache getCache() {
		return this.cache;
	}

	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}
	
	public ComponentDao getComponentDao() {
		return this.componentDao;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public ComponentKeyResolver getComponentKeyResolver() {
		return this.componentKeyResolver;
	}

	public ComponentPathResolver getComponentPathResolver() {
		return this.componentPathResolver;
	}
	
	public String[] getInitialComponentTypes() {
		return this.initialComponentTypes;
	}

	public void setInitialComponentTypes(String[] initialComponentTypes) {
		this.initialComponentTypes = initialComponentTypes;
	}

	public Integer getMinComponents() {
		return this.minComponents;
	}

	public void setMinComponents(Integer minComponents) {
		this.minComponents = minComponents;
	}

	public Integer getMaxComponents() {
		return this.maxComponents;
	}

	public void setMaxComponents(Integer maxComponents) {
		this.maxComponents = maxComponents;
	}

	public String[] getValidComponentTypes() {
		return this.validComponentTypes;
	}

	public void setValidComponentTypes(String[] validComponentTypes) {
		this.validComponentTypes = validComponentTypes;
	}

	public void setComponentRepository(ComponentRepository repository) {
		this.componentRepository = repository;
	}
	
	public ComponentRepository getComponentRepository() {
		return this.componentRepository;
	}

	public String getControllerId() {
		return this.beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setComponentKey(String key) {
		setComponentKeyResolver(new FixedComponentKeyResolver(key));
	}

	public void setComponentKeyResolver(
			ComponentKeyResolver componentKeyResolver) {
		this.componentKeyResolver = componentKeyResolver;
	}

	public void setComponentPath(String path) {
		setComponentPathResolver(new FixedComponentPathResolver(path));
	}

	public void setComponentPathResolver(
			ComponentPathResolver componentPathResolver) {
		this.componentPathResolver = componentPathResolver;
	}

	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}	
	
	public ViewModeResolver getViewModeResolver() {
		return this.viewModeResolver;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		boolean preview = viewModeResolver.isPreviewMode(request);
		final RenderStrategy strategy;
		if (preview) {
			strategy = new EditModeRenderStrategy(componentDao, componentRepository, 
					this, request, response);
			
			PageRequestUtils.storeContext(request, 120000);
		}
		else {
			strategy = new LiveModeRenderStrategy(componentDao, componentRepository, 
					this, request, response, cache);
		}
		
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					strategy.render();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		return null;
	}

	

}
