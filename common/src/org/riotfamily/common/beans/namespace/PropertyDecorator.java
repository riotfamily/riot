package org.riotfamily.common.beans.namespace;

import org.riotfamily.common.xml.XmlUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * BeanDefinitionDecorator that calls 
 * {@link BeanDefinitionParserDelegate#parsePropertySubElement(Element, BeanDefinition)
 * delegate.parsePropertySubElement()} <b>with the first child element</b> and 
 * sets the result as property. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class PropertyDecorator implements BeanDefinitionDecorator {

	private String propertyName;
	
	public PropertyDecorator() {
	}
	
	public PropertyDecorator(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {

		BeanDefinition bd = definition.getBeanDefinition();
		MutablePropertyValues pv = bd.getPropertyValues();
		Element child = XmlUtils.getFirstChildElement((Element) node);
		if (child != null) {
			Object value = parserContext.getDelegate().parsePropertySubElement(child, bd);
			pv.addPropertyValue(getPropertyName(node), value);
		}
		return definition;
	}

	private String getPropertyName(Node node) {
		if (propertyName != null) {
			return propertyName;
		}
		return XmlUtils.getLocalName(node);
	}
}