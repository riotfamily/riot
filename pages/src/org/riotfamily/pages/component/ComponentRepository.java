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
package org.riotfamily.pages.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Repository containing component implementations.
 */
public class ComponentRepository implements ServletContextAware, 
		ApplicationContextAware, InitializingBean, ConfigurableBean {

	private Log log = LogFactory.getLog(ComponentRepository.class);
	
	private String[] configLocations;
	
	private ServletContext servletContext;
	
	private ApplicationContext applicationContext;
	
	private XmlWebApplicationContext context;
	
	private Map componentMap;

	private BeanConfigurationWatcher configWatcher = 
			new BeanConfigurationWatcher(this);
	
	private HashSet formIds = new HashSet();

	public void setConfigLocations(String[] configLocations) {
		this.configLocations = configLocations;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void addListener(ConfigurationEventListener listener) {
		configWatcher.addListener(listener);
	}
	
	public void afterPropertiesSet() {
		context = new XmlWebApplicationContext();
		context.setParent(applicationContext);
		context.setServletContext(servletContext);
		context.setConfigLocations(configLocations);
		
		ArrayList resources = new ArrayList();
		for (int i = 0; i < configLocations.length; i++) {
			resources.add(applicationContext.getResource(configLocations[i]));
		}
		configWatcher.setResources(resources);
		
		configure();
	}
	
	public boolean isReloadable() {
		return true;
	}
	
	public void configure() {
		context.refresh();
		componentMap = context.getBeansOfType(Component.class);
		log.debug("Components: " + componentMap);
	}
	
	public Collection getComponents() {
		configWatcher.checkForModifications();
		return componentMap.values();
	}
	
	public Map getComponentMap() {
		configWatcher.checkForModifications();
		return componentMap;
	}
	
	public void addFormId(String formId) {
		formIds.add(formId);
	}
	
	public String getFormId(String componentType) {
		configWatcher.checkForModifications();
		if (formIds.contains(componentType)) {
			return componentType;
		}
		return null;
	}
		
	public Component getComponent(String type) {
		configWatcher.checkForModifications();
		return (Component) componentMap.get(type);
	}
	
	public Component getComponent(ComponentVersion version) {
		return getComponent(version.getType());
	}
	
}
