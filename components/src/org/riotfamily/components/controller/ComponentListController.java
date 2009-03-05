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
package org.riotfamily.components.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.controller.render.EditModeRenderStrategy;
import org.riotfamily.components.controller.render.LiveModeRenderStrategy;
import org.riotfamily.components.controller.render.RenderStrategy;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.locator.ComponentListLocator;
import org.riotfamily.riot.security.AccessController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that renders a ComponentList. Which list is to be rendered is
 * determined using a {@link ComponentListLocator}.
 */
public class ComponentListController implements Controller,
		ComponentListConfiguration, InitializingBean {

	private CacheService cacheService;

	private ComponentDao componentDao;

	private ComponentListLocator locator;

	private String[] initialComponentTypes;

	private Integer minComponents;

	private Integer maxComponents;

	private ComponentRepository componentRepository;
	
	private String[] validComponentTypes;

	private PlatformTransactionManager transactionManager;
	
	private RenderStrategy liveModeRenderStrategy;
	
	private RenderStrategy editModeRenderStrategy;

	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}

	public CacheService getCacheService() {
		return this.cacheService;
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
		if (validComponentTypes == null) {
			setValidComponentTypes(initialComponentTypes);
		}
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
		ArrayList validTypes = new ArrayList(validComponentTypes.length);
		for (int i = 0; i < validComponentTypes.length; i++) {
			if (!validTypes.contains(validComponentTypes[i])) {
				validTypes.add(validComponentTypes[i]);
			}
		}
		this.validComponentTypes = (String[]) validTypes.toArray(new String[validTypes.size()]);
	}

	public void setComponentRepository(ComponentRepository repository) {
		this.componentRepository = repository;
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

	public void afterPropertiesSet() throws Exception {
		editModeRenderStrategy = new EditModeRenderStrategy(componentDao,
				componentRepository, this);
		
		liveModeRenderStrategy = new LiveModeRenderStrategy(componentDao, 
				componentRepository, this, cacheService);
	}
	
	public ModelAndView handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					RenderStrategy strategy = liveModeRenderStrategy;
					if (EditModeUtils.isEditMode(request) &&
							AccessController.isGranted("edit", locator.getLocation(request))) {
						
						strategy = editModeRenderStrategy;
					}
					strategy.render(request, response);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

		return null;
	}


}
