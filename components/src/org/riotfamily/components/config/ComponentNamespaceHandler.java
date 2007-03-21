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
package org.riotfamily.components.config;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.beans.xml.DefinitionParserUtils;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.components.component.IncludeComponent;
import org.riotfamily.components.component.StaticComponent;
import org.riotfamily.components.component.ViewComponent;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * NamespaceHandler that handles the <code>component</code> 
 * namespace as defined in <code>component.xsd</code> which can be found in 
 * the same package.
 */
public class ComponentNamespaceHandler implements NamespaceHandler {

	
	public void init() {
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String name = element.getLocalName();
		if ("static-component".equals(name)) {
			return parseComponent(element, StaticComponent.class, 
					new String[] { "location", "onChangeScript=onchange" }, 
					parserContext);
		}
		if ("view-component".equals(name)) {
			RootBeanDefinition definition = parseComponent(element, 
					ViewComponent.class, 
					new String[] { "view-name", "dynamic", "onChangeScript=onchange" }, 
					parserContext);
			
			addPropertyProcessors(definition, element, parserContext);
			return definition;
		}
		if ("include-component".equals(name)) {
			RootBeanDefinition definition = parseComponent(element, 
					IncludeComponent.class, 
					new String[] { "uri", "dynamic", "onChangeScript=onchange" }, 
					parserContext);
			
			addPropertyProcessors(definition, element, parserContext);
			return definition;
		}
		throw new IllegalArgumentException("Element not supported: " + name);
	}
	
	private RootBeanDefinition parseComponent(Element element, Class beanClass, 
			String[] attributeNames, ParserContext parserContext) {
		
		RootBeanDefinition definition = new RootBeanDefinition();
		definition.setBeanClass(beanClass);
		
		MutablePropertyValues pv = new MutablePropertyValues();
		definition.setPropertyValues(pv);
		if (attributeNames != null) {
			DefinitionParserUtils.addProperties(pv, element, attributeNames);
		}
		
		DefinitionParserUtils.registerBeanDefinition(definition, element, 
				"id", null, parserContext);
		
		return definition;
	}
	
	private void addPropertyProcessors(RootBeanDefinition definition, 
			Element element, ParserContext parserContext) {
		
		List childElements = XmlUtils.getChildElements(element);
		if (!childElements.isEmpty()) {
			MutablePropertyValues pv = definition.getPropertyValues();
			ManagedList processors = new ManagedList();
			pv.addPropertyValue("propertyProcessors", processors);
			Iterator it = childElements.iterator();
			while (it.hasNext()) {
				Element child = (Element) it.next();
				if ("bean".equals(child.getLocalName())) {
					processors.add(parserContext.getDelegate()
							.parseBeanDefinitionElement(child, definition));
				}
			}
		}
	}
		
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder 
			holder, ParserContext parserContext) {
		
		throw new UnsupportedOperationException(
				"Bean decoration is not (yet) supported.");
	}

}
