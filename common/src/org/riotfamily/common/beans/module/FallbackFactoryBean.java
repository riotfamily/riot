package org.riotfamily.common.beans.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class FallbackFactoryBean implements FactoryBean, BeanFactoryAware, 
		BeanNameAware, InitializingBean {

	private static Log log = LogFactory.getLog(FallbackFactoryBean.class);
	
	public static final String IMPLEMENTATION_BEAN_NAME_SUFFIX = "Impl";
	
	private Object fallback;
	
	private String implementationBeanName;

	private Object implementation;
	
	private String beanName;

	private BeanFactory beanFactory;

	public void setImplementationBeanName(String implementationBeanName) {
		this.implementationBeanName = implementationBeanName;
	}

	public void setFallback(Object fallback) {
		this.fallback = fallback;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(fallback, "A fallback implementation must be provided.");
		if (implementationBeanName == null) {
			implementationBeanName = beanName + IMPLEMENTATION_BEAN_NAME_SUFFIX;
		}
		if (beanFactory.containsBean(implementationBeanName)) {
			implementation = beanFactory.getBean(implementationBeanName);
			log.info(beanName + ": Using implementation '" 
					+ implementationBeanName + "' [" 
					+ implementation.getClass().getName() + "]");
		}
		else {
			implementation = fallback;
			log.info(beanName + ": Using fallback [" 
					+ implementation.getClass().getName() + "]");
		}
	}
	
	public Object getObject() throws Exception {
		return implementation;
	}

	public Class getObjectType() {
		return implementation.getClass();
	}

	public boolean isSingleton() {
		return true;
	}

}
