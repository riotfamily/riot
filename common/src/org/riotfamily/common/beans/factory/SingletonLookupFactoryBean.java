package org.riotfamily.common.beans.factory;

import java.util.Collection;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * FactoryBean that looks up a bean of the specified type. 
 * An IllegalStateException is thrown if the ApplicationContext does not 
 * contain exactly one bean of the specified type.
 * <p>
 * Main purpose of this class is to get a reference to an anonymous 
 * bean exposed by a custom name space handler.
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SingletonLookupFactoryBean implements FactoryBean, 
		ApplicationContextAware {

	private Class<?> objectType;
	
	private ApplicationContext applicationContext;
	
	public SingletonLookupFactoryBean(Class<?> objectType) {
		this.objectType = objectType;
	}

	public Class<?> getObjectType() {
		return null;
	}
	
	public boolean isSingleton() {
		return true;
	}
	
	public Object getObject() throws Exception {
		Collection<?> beans = SpringUtils.beansOfType(applicationContext, 
				objectType, false, false).values();
		
		Assert.isTrue(beans.size() == 1, "Expected exactly one bean of type [" 
				+ objectType + "] but found " + beans.size());
		
		return beans.iterator().next();
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
