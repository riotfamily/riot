package org.riotfamily.common.beans.namespace;

import java.util.Collection;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * BeanDefinitionDecorator that calls 
 * {@link BeanDefinitionParserDelegate#parsePropertySubElement(Element, BeanDefinition)
 * delegate.parsePropertySubElement()} and adds the result to a list-property.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class ListItemDecorator implements BeanDefinitionDecorator {

	private String listPropertyName;

	public ListItemDecorator(String listPropertyName) {
		this.listPropertyName = listPropertyName;
	}

	@SuppressWarnings("unchecked")
	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {

		BeanDefinition bd = definition.getBeanDefinition();
		MutablePropertyValues pvs = bd.getPropertyValues();
		Collection<Object> c = null;
		PropertyValue pv = pvs.getPropertyValue(listPropertyName);
		if (pv != null) {
			c = (Collection<Object>) pv.getValue();
		}
		if (c == null) {
			c = new ManagedList();
			pvs.addPropertyValue(listPropertyName, c);
		}
		c.add(parserContext.getDelegate().parsePropertySubElement((Element) node, bd));
		return definition;
	}
}