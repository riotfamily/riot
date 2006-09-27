package org.riotfamily.common.beans.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Creates a list by merging the entries with the given key from all 
 * {@link org.riotfamily.common.beans.module.FactoryBeanModule modules} found in
 * the ApplicationContext.
 * 
 * @see org.riotfamily.common.beans.module.FactoryBeanModule
 */
public class ModularListFactoryBean extends AbstractFactoryBean 
		implements ApplicationContextAware {	
	
	private List sourceList;

	private Class targetListClass = ArrayList.class;
	
	private String key;
	
	private ApplicationContext applicationContext;
	
	private static Log log = LogFactory.getLog(ModularListFactoryBean.class);
	
	/**
	 * Set the source List, typically populated via XML "list" elements.
	 */
	public void setSourceList(List sourceList) {
		this.sourceList = sourceList;
	}

	/**
	 * Set the class to use for the target List. Can be populated with a fully
	 * qualified class name when defined in a Spring application context.
	 * <p>Default is a <code>java.util.ArrayList</code>.
	 * @see java.util.ArrayList
	 */
	public void setTargetListClass(Class targetListClass) {
		if (targetListClass == null) {
			throw new IllegalArgumentException("targetListClass must not be null");
		}
		if (!List.class.isAssignableFrom(targetListClass)) {
			throw new IllegalArgumentException("targetListClass must implement [java.util.List]");
		}
		this.targetListClass = targetListClass;
	}

	public Class getObjectType() {
		return java.util.List.class;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Object createInstance() {
		List result = (List) BeanUtils.instantiateClass(this.targetListClass);
		Map modules = applicationContext.getBeansOfType(
				FactoryBeanModule.class, false, false);
		
		Iterator it = modules.values().iterator();
		while (it.hasNext()) {
			FactoryBeanModule module = (FactoryBeanModule) it.next();
			List moduleList = module.getList(key);
			if (moduleList != null) {
				log.info("Adding items defined by " + module.getName() 
						+ " to " + key);
				
				result.addAll(moduleList);
			}
		}
		
		if (sourceList != null) {
			result.addAll(sourceList);
		}
		
		return result;
	}
}
