package org.riotfamily.common.beans.namespace;

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * BeanDefinitionDecorator that calls 
 * {@link BeanDefinitionParserDelegate#parseMapElement(Element, BeanDefinition)
 * delegate.parseMapElement()} and sets the resulting map as property. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class MapDecorator implements BeanDefinitionDecorator {

	private String mapPropertyName;

	public MapDecorator(String mapPropertyName) {
		this.mapPropertyName = mapPropertyName;
	}

	@SuppressWarnings("unchecked")
	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {

		BeanDefinition bd = definition.getBeanDefinition();
		Map map = parserContext.getDelegate().parseMapElement((Element) node, bd);
		bd.getPropertyValues().addPropertyValue(mapPropertyName, map);
		return definition;
	}
}