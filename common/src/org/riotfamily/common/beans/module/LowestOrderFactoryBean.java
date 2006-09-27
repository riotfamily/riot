package org.riotfamily.common.beans.module;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

public class LowestOrderFactoryBean implements FactoryBean, 
		ApplicationContextAware {

	private Class type;
	
	private ApplicationContext applicationContext;
	
	public void setType(Class type) {
		Assert.isAssignable(Ordered.class, type, "Type must implement the " +
				"'org.springframework.core.Ordered' interface.");
		
		this.type = type;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Object getObject() throws Exception {
		Assert.notNull(type, "A type must be specified.");
		
		ArrayList beans = new ArrayList(
				BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, type).values());
		
		Assert.notEmpty(beans, "At last one bean of type '" + type.getName()
				+ "' must be present.");
		
		Collections.sort(beans, new OrderComparator());
		return beans.get(0);
	}

	public Class getObjectType() {
		return type;
	}

	public boolean isSingleton() {
		return true;
	}

}
