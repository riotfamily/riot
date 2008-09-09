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
package org.riotfamily.components.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.components.config.component.ComponentRenderer;
import org.riotfamily.components.config.component.ViewComponent;
import org.riotfamily.components.locator.ComponentListLocator;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentListLocation;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
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

	private Map componentMap = new HashMap();

	private String viewNamePrefix;

	private String viewNameSuffix;

	private BeanConfigurationWatcher configWatcher =
			new BeanConfigurationWatcher(this);

	private Map controllers;

	private List locators;

	private FormRepository formRepository;

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

	public void setFormRepository(FormRepository formRepository) {
		this.formRepository = formRepository;
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

	public MessageSource getMessageSource() {
		return applicationContext;
	}

	public boolean isReloadable() {
		return true;
	}

	public void configure() {
		context.refresh();
		componentMap = context.getBeansOfType(ComponentRenderer.class);
		log.debug("Components: " + componentMap);
	}

	public void addComponent(String type, ComponentRenderer component) {
		componentMap.put(type, component);
	}

	public ComponentRenderer getComponent(String type) {
		configWatcher.checkForModifications();
		if (componentMap.get(type) == null) {
			ViewComponent viewComponent = new ViewComponent();
			String viewName = viewNamePrefix + type + viewNameSuffix;
			viewComponent.setViewName(viewName);
			componentMap.put(type, viewComponent);
		}
		return (ComponentRenderer) componentMap.get(type);
	}

	public ComponentRenderer getComponent(Component component) {
		return getComponent(component.getType());
	}

	public void setViewNamePrefix(String defaultViewLocation) {
		this.viewNamePrefix = defaultViewLocation;
	}

	public void setViewNameSuffix(String viewSuffix) {
		this.viewNameSuffix = viewSuffix;
	}

	public ComponentListConfiguration getListConfiguration(String controllerId) {
		return (ComponentListConfiguration) controllers.get(controllerId);
	}

	public void clearControllers() {
		controllers = new HashMap();
		locators = new ArrayList();
	}

	public void registerController(String name, ComponentListConfiguration controller) {
		controllers.put(name, controller);
		ComponentListLocator locator = controller.getLocator();
		locators.add(locator);
	}

	public String getUrl(ComponentList componentList) {
		ComponentListLocation location = componentList.getLocation();
		Iterator it = locators.iterator();
		while (it.hasNext()) {
			ComponentListLocator locator = (ComponentListLocator) it.next();
			if (locator.supports(location.getType())) {
				return locator.getUrl(location);
			}
		}
		return null;
	}
	
	public String getFormUrl(String formId, Long containerId) {
		if (formRepository.containsForm(formId)) {
			return "/components/form/" + formId + "/" + containerId;
		}
		return null;
	}

}