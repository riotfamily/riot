package org.riotfamily.common.beans;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class SetupBeanFactory implements InitializingBean, 
		ApplicationContextAware, ServletContextAware, BeanNameAware {

	private static Log log = LogFactory.getLog(SetupBeanFactory.class);
	
	private String displayName;
	
	private boolean performSetup = true;
	
	private String[] configLocations;
	
	private XmlWebApplicationContext context;
	
	private ServletContext servletContext;
	
	private ApplicationContext parent;
	
	public void setConfigLocations(String[] configLocations) {
		this.configLocations = configLocations;
	}

	public void setPerformSetup(boolean performSetup) {
		this.performSetup = performSetup;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.parent = applicationContext;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setBeanName(String name) {
		this.displayName = name;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (performSetup) {
			context = new XmlWebApplicationContext();
			context.setParent(parent);
			context.setDisplayName(displayName);
			context.setConfigLocations(configLocations);
			context.setServletContext(servletContext);
			log.info("Performing setup ...");
			context.refresh();
		}
		else {
			log.info("Skipping setup.");
		}
	}

	public ApplicationContext getContext() {
		return this.context;
	}
	
}
