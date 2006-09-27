package org.riotfamily.common.beans.module;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ModularMapFactoryBean extends MapFactoryBean 
		implements ApplicationContextAware {

	private static Log log = LogFactory.getLog(ModularMapFactoryBean.class);
	
	private String key;
	
	private ApplicationContext applicationContext;
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Object createInstance() {
		Map result = (Map) super.createInstance();
		Map modules = applicationContext.getBeansOfType(
				FactoryBeanModule.class, false, false);
		
		Iterator it = modules.values().iterator();
		while (it.hasNext()) {
			FactoryBeanModule module = (FactoryBeanModule) it.next();
			Map moduleMap = module.getMap(key);
			if (moduleMap != null) {
				log.info("Adding entries defined by " + module.getName() 
						+ " to " + key);
				
				result.putAll(moduleMap);
			}
		}
		return result;
	}

}
