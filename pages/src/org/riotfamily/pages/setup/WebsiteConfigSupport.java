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
package org.riotfamily.pages.setup;

import java.util.Map;

import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.menu.SitemapBuilder;
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
		return websiteConfig.getComponentRepository();
	}
	
	protected ComponentDao getDao() {
		return websiteConfig.getComponentDao();
	}
		
	protected Map getControllers() {
		return websiteConfig.getComponentListControllers();
	}
	
	protected PageMap getPageMap() {
		return websiteConfig.getPageMap();
	}
	
	protected SitemapBuilder getSitemapBuilder() {
		return websiteConfig.getSitemapBuilder();
	}
}
