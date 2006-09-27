package org.riotfamily.pages.setup;

import java.util.Map;

import org.riotfamily.cachius.Cache;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.page.PageMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class WebsiteConfigSupport implements ApplicationContextAware, 
		WebsiteConfigAware, InitializingBean {

	private ApplicationContext applicationContext;

	private WebsiteConfig websiteConfig;
	
	
	public final void setApplicationContext(
			ApplicationContext applicationContext) {
		
		this.applicationContext = applicationContext;
	}
	
	protected ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	public final void afterPropertiesSet() {
		Plumber.register(applicationContext, this);
	}
	
	public void setWebsiteConfig(WebsiteConfig config) {
		this.websiteConfig = config;
		initWebsiteConfig();
	}
	
	public WebsiteConfig getWebsiteConfig() {
		return this.websiteConfig;
	}

	protected void initWebsiteConfig() {
	}
	
	protected ComponentRepository getRepository() {
		return websiteConfig.getRepository();
	}
	
	protected Map getControllers() {
		return websiteConfig.getComponentListControllers();
	}
	
	protected Cache getCache() {
		return websiteConfig.getCache();
	}

	protected PageMap getPageMap() {
		return websiteConfig.getPageMap();
	}
}
