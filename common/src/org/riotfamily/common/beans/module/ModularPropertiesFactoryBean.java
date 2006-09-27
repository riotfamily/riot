package org.riotfamily.common.beans.module;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ModularPropertiesFactoryBean extends PropertiesFactoryBean
		implements ApplicationContextAware {

	private static Log log = LogFactory.getLog(ModularPropertiesFactoryBean.class);
	
	private String key;
	
	private ApplicationContext applicationContext;
		
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Object createInstance() throws IOException {
		Properties result = (Properties) super.createInstance();
		Map modules = applicationContext.getBeansOfType(
				FactoryBeanModule.class, false, false);

		Iterator it = modules.values().iterator();
		while (it.hasNext()) {
			FactoryBeanModule module = (FactoryBeanModule) it.next();
			Properties moduleProps = module.getProperties(key);
			if (moduleProps != null) {
				log.info("Adding entries defined by " 
						+ module.getName() + " to " + key);
				
				Enumeration en = moduleProps.propertyNames();
				while (en.hasMoreElements()) {
					String name = (String) en.nextElement();
					result.setProperty(name, moduleProps.getProperty(name));
				}
			}
		}
		return result;
	}

}
