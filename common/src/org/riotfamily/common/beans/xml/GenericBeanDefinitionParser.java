package org.riotfamily.common.beans.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.common.xml.DigesterUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class GenericBeanDefinitionParser implements BeanDefinitionParser {

	public static final String ID_ATTRIBUTE = "id";
	
	public static final String NAME_ATTRIBUTE = "name";
	
	public static final String BEAN_ELEMENT = "bean";
	
	public static final String REF_ATTRIBUTE = "ref";

	private Log log = LogFactory.getLog(GenericBeanDefinitionParser.class);
	
	private HashMap classes = new HashMap();
	
	public void registerElement(String name, Class clazz) {
		classes.put(name, clazz);
	}
	
	protected Class getBeanClass(Element element) {
		String name = element.getLocalName();
		Class clazz = (Class) classes.get(name);
		Assert.notNull(clazz, "No class registered for element " + name);
		log.debug("Class for element [" + name + "] is " + clazz);
		return clazz;
	}
	
	protected String extractPropertyName(String attributeName) {
		return FormatUtils.xmlToCamelCase(attributeName);
	}
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition definition = parseDefinition(
				element, parserContext.getRegistry());
		
		DefinitionParserUtils.registerBeanDefinition(definition, element, 
				ID_ATTRIBUTE, NAME_ATTRIBUTE, parserContext);
		
		return definition;
	}
	
	private RootBeanDefinition parseDefinition(Element element, 
			BeanDefinitionRegistry registry) {
		
		RootBeanDefinition definition = new RootBeanDefinition();
		Class beanClass = getBeanClass(element);
		definition.setBeanClass(beanClass);
		MutablePropertyValues pv = new MutablePropertyValues();
		definition.setPropertyValues(pv);
		
		NamedNodeMap attributes = element.getAttributes();
		for (int x = 0; x < attributes.getLength(); x++) {
			Attr attribute = (Attr) attributes.item(x);
			String name = attribute.getLocalName();
			if (ID_ATTRIBUTE.equals(name) || NAME_ATTRIBUTE.equals(name)) {
				continue;
			}
			pv.addPropertyValue(extractPropertyName(name), attribute.getValue());
		}
		
		List children = DigesterUtils.getChildElements(element);
		Iterator it = children.iterator();
		while (it.hasNext()) {
			Element child = (Element) it.next();
			String property = extractPropertyName(child.getLocalName());
			Class type = PropertyUtils.getPropertyType(beanClass, property);
			Object value = collectionOrReference(child, type, registry);
			pv.addPropertyValue(property, value);
		}
		
		return definition;
	}
	
	protected Object collectionOrReference(Element element, Class type, 
			BeanDefinitionRegistry registry) {
		
		if (List.class.isAssignableFrom(type)) {
			ManagedList list = new ManagedList();
			List children = DigesterUtils.getChildElements(element);
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element child = (Element) it.next();
				list.add(parseReference(child, registry));
			}
			return list;
		}
		else {
			String ref = DigesterUtils.getAttribute(element, REF_ATTRIBUTE);
			if (ref != null) {
				return new RuntimeBeanReference(ref);
			}
			Element child = DigesterUtils.getFirstChildElement(element);
			return parseReference(child, registry); 
		}
	}
		
	protected RuntimeBeanReference parseReference(Element element, 
			BeanDefinitionRegistry registry) {
		
		String beanName;
		if (element.getLocalName().equals(BEAN_ELEMENT)) {
			beanName = element.getAttribute(REF_ATTRIBUTE);
		}
		else {
			RootBeanDefinition definition = parseDefinition(element, registry);
			beanName = BeanDefinitionReaderUtils.generateBeanName(
					definition, registry, true);
			
			registry.registerBeanDefinition(beanName, definition);
		}
		return new RuntimeBeanReference(beanName);
	}
	
}
