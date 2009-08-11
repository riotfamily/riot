package org.riotfamily.common.beans.namespace;

import java.util.Properties;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * BeanDefinitionDecorator that calls 
 * {@link BeanDefinitionParserDelegate#parsePropsElement(Element)
 * delegate.parsePropsElement()} and sets the result as property. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PropsDecorator implements BeanDefinitionDecorator {

	private String propertyName;

	public PropsDecorator(String propertyName) {
		this.propertyName = propertyName;
	}

	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {

		BeanDefinition bd = definition.getBeanDefinition();
		Properties props = parserContext.getDelegate().parsePropsElement((Element) node);
		bd.getPropertyValues().addPropertyValue(propertyName, props);
		return definition;
	}
}
