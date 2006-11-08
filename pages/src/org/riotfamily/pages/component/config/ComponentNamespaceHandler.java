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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component.config;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.riotfamily.common.beans.xml.DefinitionParserUtils;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.pages.component.impl.IncludeComponent;
import org.riotfamily.pages.component.impl.StaticComponent;
import org.riotfamily.pages.component.impl.ViewComponent;
import org.riotfamily.pages.component.property.DefaultValuePropertyProcessor;
import org.riotfamily.pages.component.property.HibernatePropertyProcessor;
import org.riotfamily.pages.component.property.PropertyEditorProcessor;
import org.riotfamily.pages.component.property.XmlPropertyProcessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * NamespaceHandler that handles the <code>component</code> 
 * namspace as defined in <code>component.xsd</code> which can be found in 
 * the same package.
 */
public class ComponentNamespaceHandler implements NamespaceHandler {

	
	public void init() {
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String name = element.getLocalName();
		if ("static-component".equals(name)) {
			return parseBean(element, StaticComponent.class, 
					new String[] { "location" }, "id",
					parserContext);
		}
		if ("view-component".equals(name)) {
			RootBeanDefinition definition = parseBean(element, 
					ViewComponent.class, 
					new String[] { "view-name", "dynamic" }, "id",
					parserContext);
			
			addPropertyProcessors(definition, element, parserContext);
			return definition;
		}
		if ("include-component".equals(name)) {
			RootBeanDefinition definition = parseBean(element, 
					IncludeComponent.class, 
					new String[] { "uri", "dynamic" }, "id",
					parserContext);
			
			addPropertyProcessors(definition, element, parserContext);
			return definition;
		}
		throw new IllegalArgumentException("Element not supported: " + name);
	}
	
	protected RootBeanDefinition parseBean(Element element, Class beanClass, 
			String[] attributeNames, String idProperty, 
			ParserContext parserContext) {
		
		RootBeanDefinition definition = new RootBeanDefinition();
		definition.setBeanClass(beanClass);
		
		MutablePropertyValues pv = new MutablePropertyValues();
		definition.setPropertyValues(pv);
		if (attributeNames != null) {
			DefinitionParserUtils.addProperties(pv, element, attributeNames);
		}
		
		if (idProperty != null) {
			DefinitionParserUtils.registerBeanDefinition(definition, element, 
				idProperty, null, parserContext);
		}
		
		return definition;
	}
	
	protected void addPropertyProcessors(RootBeanDefinition definition, 
			Element element, ParserContext parserContext) {
		
		List childElements = XmlUtils.getChildElements(element);
		if (!childElements.isEmpty()) {
			MutablePropertyValues pv = definition.getPropertyValues();
			ManagedList processors = new ManagedList();
			pv.addPropertyValue("propertyProcessors", processors);
			Iterator it = childElements.iterator();
			while (it.hasNext()) {
				Element child = (Element) it.next();
				addPropertyProcessor(processors, child, parserContext);
			}
		}
	}
	
	protected void addPropertyProcessor(List propertyProcessors, 
			Element element, ParserContext parserContext) {
	
		String name = element.getLocalName();
		RuntimeBeanReference processor = null;
		if ("property-processor".equals(name)) {
			processor = new RuntimeBeanReference(element.getAttribute("ref"));
		}
		else if ("defaults".equals(name)) {
			RootBeanDefinition definition = parseBean(element, 
					DefaultValuePropertyProcessor.class, 
					null, null, parserContext);
		
			ManagedProperties defaults = new ManagedProperties();
			definition.getPropertyValues().addPropertyValue("values", defaults);
			addProps(defaults, element);
			
			processor = DefinitionParserUtils.registerAnonymousBeanDefinition(
					definition, parserContext);	
		}
		else if ("property-editor".equals(name)) {
			RootBeanDefinition definition = parseBean(element, 
					PropertyEditorProcessor.class, 
					new String[] { "property", "propertyEditor=@ref", "defaultValue=default" }, 
					null, parserContext);
			
			processor = DefinitionParserUtils.registerAnonymousBeanDefinition(
					definition, parserContext);
		}
		else if ("hibernate-entity".equals(name)) {
			RootBeanDefinition definition = parseBean(element, 
					HibernatePropertyProcessor.class, 
					new String[] { "property", "@session-factory", "entityClass=class" }, 
					null, parserContext);
			
			processor = DefinitionParserUtils.registerAnonymousBeanDefinition(
					definition, parserContext);
		}
		else if ("xml".equals(name)) {
			RootBeanDefinition definition = parseBean(element, 
					XmlPropertyProcessor.class, 
					new String[] { "property" }, 
					null, parserContext);
			
			processor = DefinitionParserUtils.registerAnonymousBeanDefinition(
					definition, parserContext);
		}
		propertyProcessors.add(processor);
	}
	
	protected void addProps(Properties props, Element element) {
		List propElements = DomUtils.getChildElementsByTagName(element, "property");
		Iterator it = propElements.iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String name = XmlUtils.getAttribute(ele, "name");
			String value = XmlUtils.getAttribute(ele, "value");
			props.setProperty(name, value);
		}
	}
	
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder 
			holder, ParserContext parserContext) {
		
		throw new UnsupportedOperationException(
				"Bean decoration is not (yet) supported.");
	}

}
