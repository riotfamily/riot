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
