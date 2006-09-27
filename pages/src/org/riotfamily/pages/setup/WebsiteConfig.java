package org.riotfamily.pages.setup;

import java.util.Map;

import org.riotfamily.cachius.Cache;
import org.riotfamily.pages.component.ComponentListController;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.page.PageMap;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Usage: Place an instance of this class in your website servlet's 
 * application context.
 */
public class WebsiteConfig implements ApplicationContextAware, InitializingBean {
	
	private Plumber plumber;
	
	private ComponentRepository repository;
	
	private Map componentListControllers;
	
	private PageMap pageMap;
	
	private Cache cache;
	
	public WebsiteConfig(ComponentRepository repository) {
		this.repository = repository;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		plumber = (Plumber) BeanFactoryUtils.beanOfType(
				applicationContext.getParent(),	Plumber.class);
		
		componentListControllers = applicationContext.getBeansOfType(
				ComponentListController.class);
		
		try {
			pageMap = (PageMap) BeanFactoryUtils.beanOfType(
					applicationContext,	PageMap.class);
		}
		catch (NoSuchBeanDefinitionException e) {
		}
	}

	public Cache getCache() {
		return this.cache;
	}
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public Map getComponentListControllers() {
		return this.componentListControllers;
	}

	public PageMap getPageMap() {
		return this.pageMap;
	}

	public ComponentRepository getRepository() {
		return this.repository;
	}

	public void afterPropertiesSet() throws Exception {
		plumber.setWebsiteConfig(this);
	}

}
