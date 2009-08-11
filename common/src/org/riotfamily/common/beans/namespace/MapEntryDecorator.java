package org.riotfamily.common.beans.namespace;

import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * BeanDefinitionDecorator that calls 
 * {@link BeanDefinitionParserDelegate#parsePropertyValue(Element, BeanDefinition, String)
 * delegate.parsePropertyValue()} and puts the result into a map.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class MapEntryDecorator implements BeanDefinitionDecorator {

	private String mapPropertyName;

	private String keyAttribute;
	
	public MapEntryDecorator(String mapPropertyName, String keyAttribute) {
		this.mapPropertyName = mapPropertyName;
		this.keyAttribute = keyAttribute;
	}

	@SuppressWarnings("unchecked")
	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {

		BeanDefinition bd = definition.getBeanDefinition();
		MutablePropertyValues pvs = bd.getPropertyValues();
		Map<Object,Object> map = null;
		PropertyValue pv = pvs.getPropertyValue(mapPropertyName);
		if (pv != null) {
			map = (Map<Object,Object>) pv.getValue();
		}
		if (map == null) {
			map = new ManagedMap();
			pvs.addPropertyValue(mapPropertyName, map);
		}
		Element ele = (Element) node;
		String key = ele.getAttribute(keyAttribute);
		map.put(key, parserContext.getDelegate().parsePropertyValue(ele, bd, key));
		return definition;
	}
}