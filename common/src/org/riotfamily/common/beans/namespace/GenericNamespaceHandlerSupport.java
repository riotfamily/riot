package org.riotfamily.common.beans.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class GenericNamespaceHandlerSupport extends NamespaceHandlerSupport {

	/**
	 * Registers a {@link GenericBeanDefinitionParser} for the given elementName
	 * that creates BeanDefinitions for the specified class.
	 */
	protected GenericBeanDefinitionParser register(String elementName, Class<?> beanClass) {
		GenericBeanDefinitionParser parser = new GenericBeanDefinitionParser(beanClass);
		registerBeanDefinitionParser(elementName, parser);
		return parser;
	}
	
	/**
	 * Registers a {@link GenericBeanDefinitionParser} for the given elementName.
	 * The bean class is passed as string to avoid runtime dependencies. If a
	 * dependency is missing, a warning is logged and the element is ignored. 
	 */
	protected GenericBeanDefinitionParser register(String elementName, 
			String className) {
		
		GenericBeanDefinitionParser parser = new GenericBeanDefinitionParser(className);
		registerBeanDefinitionParser(elementName, parser);
		return parser;
	}
	
}
