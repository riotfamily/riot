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
 *   Jan-Frederic Linde <jfl@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.config;

import java.util.Iterator;
import java.util.Map;

import org.riotfamily.cachius.Cache;
import org.riotfamily.components.ComponentController;
import org.riotfamily.components.ComponentListController;
import org.riotfamily.components.ComponentListLocator;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.editor.ComponentFormRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

public class ControllerConfigurer implements ApplicationContextAware, 
		InitializingBean {
	
	private ApplicationContext applicationContext;
	
	private Cache cache;

	private ComponentDao componentDao;
	
	private ComponentListLocator locator;
	
	private ComponentRepository repository;
	
	private ComponentFormRegistry formRegistry; 
	
	private PlatformTransactionManager transactionManager;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}
	
	public void setLocator(ComponentListLocator locator) {
		this.locator = locator;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setRepository(ComponentRepository repository) {
		this.repository = repository;
	}	
	
	public void setFormRegistry(ComponentFormRegistry formRegistry) {
		this.formRegistry = formRegistry;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(transactionManager, "A transactionManager must be set.");
		configureComponenControllers();
		configureComponentListControllers();
	}
	
	protected void configureComponenControllers() {
		Map controllers = applicationContext.getBeansOfType(
						ComponentController.class);
		
		Iterator it = controllers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			ComponentController controller = 
					(ComponentController) entry.getValue();
			
			controller.setTransactionManager(transactionManager);
			
			if (controller.getComponentDao() == null) {
				controller.setComponentDao(componentDao);
			}
			if (controller.getComponentRepository() == null) {
				controller.setComponentRepository(repository);
			}
		}
	}
	
	protected void configureComponentListControllers() {
		repository.clearControllers();
		Map controllers = applicationContext.getBeansOfType(
						ComponentListController.class);
		
		Iterator it = controllers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			ComponentListController controller = 
						(ComponentListController) entry.getValue();
			
			controller.setTransactionManager(transactionManager);
			
			if (controller.getCache() == null) {
				controller.setCache(cache);
			}
			if (controller.getComponentDao() == null) {
				controller.setComponentDao(componentDao);
			}
			if (controller.getComponentRepository() == null) {
				controller.setComponentRepository(repository);
			}
			if (controller.getFormRegistry() == null) {
				controller.setFormRegistry(formRegistry);
			}
			if (controller.getLocator() == null) {
				controller.setLocator(locator);
			}
			
			String controllerId = (String) entry.getKey();
			repository.registerController(controllerId, controller);
			String[] aliases = applicationContext.getAliases(controllerId);
			for (int i = 0; i < aliases.length; i++) {
				repository.registerController(aliases[i], controller);	
			}
		}
	}
	
}
