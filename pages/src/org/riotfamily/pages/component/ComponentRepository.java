package org.riotfamily.pages.component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Repository containing component implementations.
 */
public class ComponentRepository implements ServletContextAware, 
		ApplicationContextAware, InitializingBean {

	private Log log = LogFactory.getLog(ComponentRepository.class);
	
	private String[] configLocations;
	
	private ServletContext servletContext;
	
	private ApplicationContext applicationContext;
	
	private Map componentMap;

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
	
	public void afterPropertiesSet() {
		XmlWebApplicationContext context = new XmlWebApplicationContext();
		context.setParent(applicationContext);
		context.setServletContext(servletContext);
		context.setConfigLocations(configLocations);
		context.refresh();
		componentMap = context.getBeansOfType(Component.class);
		log.debug("Components: " + componentMap);
	}
	
	public Collection getComponents() {
		return componentMap.values();
	}
	
	public Map getComponentMap() {
		return componentMap;
	}
	
	public void addFormId(String formId) {
		formIds.add(formId);
	}
	
	public String getFormId(String componentType) {
		if (formIds.contains(componentType)) {
			return componentType;
		}
		return null;
	}
		
	public Component getComponent(String type) {
		return (Component) componentMap.get(type);
	}
	
	public Component getComponent(ComponentVersion version) {
		return (Component) componentMap.get(version.getType());
	}
	
}
