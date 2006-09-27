package org.riotfamily.common.beans.module;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.Assert;

/**
 * This class is used by Riot modules to define lists or maps that will be
 * merged with collections defined by other modules.
 *  
 * @see org.riotfamily.common.beans.module.ModularListFactoryBean
 * @see org.riotfamily.common.beans.module.ModularMapFactoryBean
 * @see org.riotfamily.common.beans.module.ModularPropertiesFactoryBean
 */
public class FactoryBeanModule implements BeanNameAware {

	private Map properties;
		
	private String beanName;

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getName() {
		return beanName;
	}

	public List getList(String key) {
		Object value = properties.get(key);
		if (value == null) {
			return null;
		}
		if (value instanceof List) {
			return (List) value;
		}
		else {
			return Collections.singletonList(value);
		}
	}
	
	public Map getMap(String key) {
		Object value = properties.get(key);
		if (value == null) {
			return null;
		}
		Assert.isInstanceOf(Map.class, value, "Module property [" + key 
				+ "] is not a java.util.Map: " + value.getClass().getName());
		
		return (Map) value;
	}
	
	public Properties getProperties(String key) {
		Object value = properties.get(key);
		if (value == null) {
			return null;
		}
		Assert.isInstanceOf(Properties.class, value, "Module property [" + key 
				+ "] is not a java.util.Properties: " 
				+ value.getClass().getName());
		
		return (Properties) value;
	}
	

	public void setProperties(Map properties) {
		this.properties = properties;
	}

}
