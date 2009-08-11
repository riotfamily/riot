package org.riotfamily.common.beans.injection;

import java.util.Map;

import org.riotfamily.common.collection.TypeComparatorUtils;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.ClassUtils;

/**
 * BeanConfigurer implementation that uses Spring prototype beans to
 * inject dependencies.
 * <p>
 * Looks for prototype bean definitions who's class is a sub-class of 
 * {@link ConfigurableBean}. If at least one such prototype is found, 
 * {@link ConfigurableBean#configurer} is set to <code>this</code>.
 * In order to configure a ConfigurableBean instance, 
 * {@link ConfigurableListableBeanFactory#configureBean(Object, String)} is
 * invoked, passing the bean and the beanName of the matching prototype.
 * </p>  
 * @see ConfigurableListableBeanFactory#configureBean(Object, String)
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class SpringBeanConfigurer implements BeanConfigurer, BeanFactoryPostProcessor {

	private ConfigurableListableBeanFactory beanFactory;
	
	private Map<Class<?>, String> prototypes = Generics.newConcurrentHashMap();

	/**
	 * Looks for prototype beans who's class extends ConfigurableBean.
	 */
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		this.beanFactory = beanFactory;
		for (String name : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition bd = beanFactory.getMergedBeanDefinition(name);
			if (bd instanceof RootBeanDefinition) {
				RootBeanDefinition rbd = (RootBeanDefinition) bd;
				if (rbd.isPrototype()) {
					try {
						Class<?> beanClass = rbd.resolveBeanClass(beanFactory.getBeanClassLoader());
						if (beanClass != null && ConfigurableBean.class.isAssignableFrom(beanClass)) {
							prototypes.put(beanClass, name);
						}
					}
					catch (ClassNotFoundException ex) {
						throw new CannotLoadBeanClassException(
								null, name, rbd.getBeanClassName(), ex);
					}
				}
			}
		}
		if (!prototypes.isEmpty()) {
			ConfigurableBean.configurer = this;
		}
	}
	
	/**
	 * Invokes {@link ConfigurableListableBeanFactory#configureBean(Object, String)}
	 * if a matching prototype definition exists.
	 */
	public void configure(ConfigurableBean bean) {
		String prototype = getPrototype(bean);
		if (prototype != null) {
			beanFactory.configureBean(bean, prototype);
		}
	}
	
	protected String getPrototype(ConfigurableBean bean) {
		Class<?> beanClass = ClassUtils.getUserClass(bean);
		String prototype = prototypes.get(beanClass);
		if (prototype == null) {
			Class<?> mostSpecificClass = 
					TypeComparatorUtils.findNearestSuperClass(
					prototypes.keySet(), beanClass);
			
			if (mostSpecificClass != null) {
				prototype = prototypes.get(mostSpecificClass);
			}
			else {
				prototype = "";
			}
			prototypes.put(beanClass, prototype);
		}
		if (prototype.length() > 0) {
			return prototype;
		}
		return null;
	}
}
