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
package org.riotfamily.pages.component.config;

import java.util.Iterator;
import java.util.Map;

import org.riotfamily.cachius.Cache;
import org.riotfamily.pages.component.ComponentListController;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.component.resolver.ComponentKeyResolver;
import org.riotfamily.pages.component.resolver.ComponentPathResolver;
import org.riotfamily.pages.component.resolver.TemplateComponentKeyResolver;
import org.riotfamily.pages.component.resolver.UrlComponentPathResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;

public class ComponentListConfigurer implements BeanFactoryAware, 
		InitializingBean {
	
	private static final ComponentKeyResolver DEFAULT_KEY_RESOLVER = 
		new TemplateComponentKeyResolver();
	
	private static final ComponentPathResolver DEFAULT_PATH_RESOLVER = 
		new UrlComponentPathResolver();	
	
	private ListableBeanFactory beanFactory;
	
	private Cache cache;

	private ComponentDao componentDao;
	
	private ComponentRepository repository;
	
	private ViewModeResolver viewModeResolver;
	
	private ComponentKeyResolver componentKeyResolver;

	private ComponentPathResolver componentPathResolver;
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ListableBeanFactory) {		
			this.beanFactory = (ListableBeanFactory) beanFactory;
		}
		else {
			throw new IllegalArgumentException(
					"BeanFactory must be a ListableBeanFactory");
		}
	}
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}
	
	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}
	
	public void setRepository(ComponentRepository repository) {
		this.repository = repository;
	}	
	
	public void setComponentKeyResolver(ComponentKeyResolver componentKeyResolver) {
		this.componentKeyResolver = componentKeyResolver;
	}

	public void setComponentPathResolver(ComponentPathResolver componentPathResolver) {
		this.componentPathResolver = componentPathResolver;
	}
	
	public void afterPropertiesSet() throws Exception {		
		
		Map controllers = beanFactory.getBeansOfType(
						ComponentListController.class);		
		Iterator it = controllers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			ComponentListController controller = 
						(ComponentListController) entry.getValue();
			if (controller.getCache() == null) {
				controller.setCache(cache);
			}
			if (controller.getComponentDao() == null) {
				controller.setComponentDao(componentDao);
			}
			if (controller.getComponentRepository() == null) {
				controller.setComponentRepository(repository);
			}
			if (controller.getViewModeResolver() == null) {
				controller.setViewModeResolver(viewModeResolver);
			}
			if (controller.getComponentKeyResolver() == null) {
				if (componentKeyResolver != null) {					
					controller.setComponentKeyResolver(componentKeyResolver);
				}
				else {
					controller.setComponentKeyResolver(DEFAULT_KEY_RESOLVER);
				}
			}			
			if (controller.getComponentPathResolver() == null) {
				if (componentPathResolver != null) {
					controller.setComponentPathResolver(componentPathResolver);
				}
				else {
					controller.setComponentPathResolver(DEFAULT_PATH_RESOLVER);
				}
			}
		}
	}
	
}
