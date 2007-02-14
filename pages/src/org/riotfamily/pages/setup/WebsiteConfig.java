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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.setup;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.pages.component.ComponentListController;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.menu.SitemapBuilder;
import org.riotfamily.pages.page.PageMap;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Usage: Place an instance of this class in your website-servlet's context.
 */
public class WebsiteConfig implements ApplicationContextAware,
	InitializingBean {
	
	private static final Log log = LogFactory.getLog(WebsiteConfig.class);
	
	private Plumber plumber;
	
	private ComponentRepository componentRepository;
	
	private ComponentDao componentDao;
	
	private Map componentListControllers;
	
	private PageMap pageMap;
	
	private SitemapBuilder sitemapBuilder;
	
	public WebsiteConfig(ComponentRepository repository, ComponentDao dao) {
		this.componentRepository = repository;
		this.componentDao = dao;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		plumber = (Plumber) BeanFactoryUtils.beanOfType(
				applicationContext.getParent(),	Plumber.class);
		
		componentListControllers = applicationContext.getBeansOfType(
				ComponentListController.class);
		
		try {
			pageMap = (PageMap) BeanFactoryUtils.beanOfType(
					applicationContext,	PageMap.class);
			sitemapBuilder = (SitemapBuilder) BeanFactoryUtils.beanOfType(
					applicationContext,	SitemapBuilder.class);
		}
		catch (NoSuchBeanDefinitionException e) {
			log.info("Pagemap or sitemap not found in bean factory", e);
		}
	}

	public Map getComponentListControllers() {
		return this.componentListControllers;
	}

	public PageMap getPageMap() {
		return this.pageMap;
	}
	
	public SitemapBuilder getSitemapBuilder() {
		return this.sitemapBuilder;
	}

	public ComponentRepository getComponentRepository() {
		return this.componentRepository;
	}
	
	public ComponentDao getComponentDao() {
		return this.componentDao;
	}
	
	public void afterPropertiesSet() throws Exception {
		plumber.setWebsiteConfig(this);
	}

}
