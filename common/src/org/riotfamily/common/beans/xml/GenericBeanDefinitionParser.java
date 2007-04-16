/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.beans.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.xml.XmlUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
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
		RootBeanDefinition definition = parseDefinition(element, parserContext);
		DefinitionParserUtils.registerBeanDefinition(definition, element, 
				ID_ATTRIBUTE, NAME_ATTRIBUTE, parserContext);
		
		return definition;
	}
	
	private RootBeanDefinition parseDefinition(Element element, 
			ParserContext parserContext) {
		
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
		
		List children = XmlUtils.getChildElements(element);
		Iterator it = children.iterator();
		while (it.hasNext()) {
			Element child = (Element) it.next();
			String property = extractPropertyName(child.getLocalName());
			Class type = PropertyUtils.getPropertyType(beanClass, property);
			Object value;
			if (List.class.isAssignableFrom(type)) {
				value = parserContext.getDelegate().parseListElement(child, definition);
			}
			else {
				value = parseBeanOrReference(child, parserContext, definition);
			}
			pv.addPropertyValue(property, value);
		}
		
		return definition;
	}
	
	protected Object parseBeanOrReference(Element element, 
			ParserContext parserContext, RootBeanDefinition definition) {
		
		String ref = XmlUtils.getAttribute(element, REF_ATTRIBUTE);
		if (ref != null) {
			return new RuntimeBeanReference(ref);
		}
		else {
			Element beanEle = XmlUtils.getFirstChildElement(element);
			if (beanEle.getTagName().equals(BEAN_ELEMENT)) {
				return parserContext.getDelegate().parseBeanDefinitionElement(
						beanEle, definition); 
			}
			else {
				return parserContext.getDelegate().parseCustomElement(
						beanEle, definition);
			}
		}
	}
	
}
