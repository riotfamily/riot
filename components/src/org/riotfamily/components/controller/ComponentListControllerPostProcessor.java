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

import org.riotfamily.cachius.CacheService;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.locator.ComponentListLocator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentListControllerPostProcessor 
		implements ApplicationContextAware, BeanPostProcessor {
	
	
	private ApplicationContext applicationContext;

	private CacheService cacheService;

	private ComponentDao componentDao;

	private ComponentListLocator locator;

	private ComponentRepository repository;

	private PlatformTransactionManager transactionManager;
	

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
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
		repository.clearControllers();
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		
		if (bean instanceof ComponentListController) {
			initController((ComponentListController) bean, beanName);
		}
		return bean;
	}
	
	private void initController(ComponentListController controller, 
			String beanName) {
		
		controller.setTransactionManager(transactionManager);
		if (controller.getCacheService() == null) {
			controller.setCacheService(cacheService);
		}
		if (controller.getComponentDao() == null) {
			controller.setComponentDao(componentDao);
		}
		if (controller.getComponentRepository() == null) {
			controller.setComponentRepository(repository);
		}
		if (controller.getLocator() == null) {
			controller.setLocator(locator);
		}

		repository.registerController(beanName, controller);
		String[] aliases = applicationContext.getAliases(beanName);
		for (int i = 0; i < aliases.length; i++) {
			repository.registerController(aliases[i], controller);
		}
	}
	
	public Object postProcessAfterInitialization(Object bean, String beanName) 
			throws BeansException {
		
		return bean;
	}

}
