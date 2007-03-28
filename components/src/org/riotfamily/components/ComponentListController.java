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
package org.riotfamily.components;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.Cache;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.editor.ComponentFormRegistry;
import org.riotfamily.components.render.EditModeRenderStrategy;
import org.riotfamily.components.render.LiveModeRenderStrategy;
import org.riotfamily.components.render.RenderStrategy;
import org.riotfamily.riot.security.AccessController;
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
public class ComponentListController implements Controller,
		ComponentListConfiguration {	
	
	public static final String LIVE_MODE_ATTRIBUTE = 
			ComponentListController.class.getName() + ".liveMode";
	
	private static final Log log = LogFactory.getLog(
			ComponentListController.class);
	
	private Cache cache;

	private ComponentDao componentDao;

	private ComponentListLocator locator;

	private String[] initialComponentTypes;

	private Integer minComponents;
	
	private Integer maxComponents;

	private ComponentRepository componentRepository;
	
	private ComponentFormRegistry formRegistry;

	private String[] validComponentTypes;

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
	
	public ComponentFormRegistry getFormRegistry() {
		return this.formRegistry;
	}

	public void setFormRegistry(ComponentFormRegistry formRegistry) {
		this.formRegistry = formRegistry;
	}

	public ComponentRepository getComponentRepository() {
		return this.componentRepository;
	}

	public ComponentListLocator getLocator() {
		return this.locator;
	}

	public void setLocator(ComponentListLocator locator) {
		this.locator = locator;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		final RenderStrategy strategy;
		if (AccessController.isAuthenticatedUser() 
				&& request.getAttribute(LIVE_MODE_ATTRIBUTE) == null) {
			
			log.debug("Authenticated user - rendering list in edit-mode");
			strategy = new EditModeRenderStrategy(componentDao, 
					componentRepository, formRegistry, this, request, response);
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
