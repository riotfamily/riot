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
package org.riotfamily.common.beans.xml;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.xml.XmlUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public final class DefinitionParserUtils {

	private DefinitionParserUtils() {
	}
	
	public static void registerBeanDefinition(BeanDefinition definition,
			Element element, String idAttribute,  ParserContext parserContext) {
		
		registerBeanDefinition(definition, element, idAttribute, null, parserContext);
	}
	
	public static void registerBeanDefinition(BeanDefinition definition,
			Element element, String idAttribute, String aliasAttribute, 
			ParserContext parserContext) {
		
		String id = XmlUtils.getAttribute(element, idAttribute);
		
		String alias = null;
		if (aliasAttribute != null) {
			alias = XmlUtils.getAttribute(element, aliasAttribute);
		}
		
		if (id == null) {
			BeanDefinitionRegistry registry = parserContext.getRegistry();
			id = generateBeanName(definition, registry, false);
		}
		
		BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, 
				alias != null ? new String[] { alias } : null);
				
		BeanDefinitionReaderUtils.registerBeanDefinition(
				holder, parserContext.getRegistry());
		
		parserContext.getReaderContext().fireComponentRegistered(
				new BeanComponentDefinition(holder));
		
	}
	
	public static RuntimeBeanReference registerAnonymousBeanDefinition(
			BeanDefinition definition, ParserContext parserContext) {
		
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		String beanName = generateBeanName(definition, registry, true);
			
		registry.registerBeanDefinition(beanName, definition);
		return new RuntimeBeanReference(beanName);
	}
	
	/**
	 * Generates a bean name that is unique within the given bean factory.
	 * Does the same as {@link BeanDefinitionReaderUtils#generateBeanName}
	 * but takes a BeanDefinition as argument (instead of an AbstractBeanDefinition).
	 * Therefore name generation will fail for factory beans that use the
	 * 'factory-bean' attribute.
	 */
	public static String generateBeanName(
			BeanDefinition beanDefinition, BeanDefinitionRegistry beanFactory,
			boolean isInnerBean) throws BeanDefinitionStoreException {

		String generatedId = beanDefinition.getBeanClassName();
		if (generatedId == null) {
			if (beanDefinition instanceof ChildBeanDefinition) {
				generatedId = ((ChildBeanDefinition) beanDefinition).getParentName() + "$child";
			}
		}
		if (!StringUtils.hasText(generatedId)) {
			throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), "",
					"Unnamed bean definition specifies neither 'class' nor 'parent'" +
					" - can't generate bean name");
		}

		String id = generatedId;
		if (isInnerBean) {
			// Inner bean: generate identity hashcode suffix.
			id = generatedId + BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR 
					+ ObjectUtils.getIdentityHexString(beanDefinition);
		}
		else {
			// Top-level bean: use plain class name. If not already unique,
			// add counter - increasing the counter until the name is unique.
			int counter = 0;
			while (beanFactory.containsBeanDefinition(id)) {
				counter++;
				id = generatedId 
						+ BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR
						+ counter;
			}
		}
		return id;
	}
	
	/**
	 * Adds a RuntimeBeanReference to the the given PropertyValues.
	 * 
	 * @param pv PropertyValues where the reference should be added 
	 * @param element Element that provides the referenced bean name as attribute
	 * @param attribute Name of the attribute that contains the bean name
	 * @param property Name of the property to be added
	 * @return Returns <code>true</code> if the attribute was present, 
	 * 		<code>false</code> otherwise
	 */
	public static boolean addReference(MutablePropertyValues pv, 
			Element element, String attribute, String property) {
		
		String beanName = element.getAttribute(attribute);
		if (StringUtils.hasText(beanName)) {
			pv.addPropertyValue(property, new RuntimeBeanReference(beanName));
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a RuntimeBeanReference to the the given PropertyValues.
	 * The property name is derived from the attribute name using 
	 * {@link FormatUtils#xmlToCamelCase(String) FormatUtils.xmlToCamelCase()}.
	 *  
	 * @param pv PropertyValues where the reference should be added 
	 * @param element Element that provides the referenced bean name as attribute
	 * @param attribute Name of the attribute that contains the bean name
	 * @return
	 */
	public static boolean addReference(MutablePropertyValues pv, 
			Element element, String attribute) {
	
		return addReference(pv, element, attribute, 
				FormatUtils.xmlToCamelCase(attribute));
	}
	
	public static boolean addString(MutablePropertyValues pv, 
			Element element, String attribute) {
	
		return addString(pv, element, attribute, 
				FormatUtils.xmlToCamelCase(attribute));
	}
	
	public static boolean addString(MutablePropertyValues pv, 
			Element element, String attribute, String property) {
		
		String value = element.getAttribute(attribute);
		if (StringUtils.hasText(value)) {
			pv.addPropertyValue(property, value);
			return true;
		}
		return false;
	}
	
	public static void addProperties(MutablePropertyValues pv, 
			Element element, String[] attributeNames) {
		
		for (int i = 0; i < attributeNames.length; i++) {
			addProperty(pv, element, attributeNames[i]);
		}
	}
	
	public static void addProperty(MutablePropertyValues pv, 
			Element element, String attr) {
		
		String property = null;
		int i = attr.indexOf('=');
		if (i != -1) {
			property = attr.substring(0, i);
			attr = attr.substring(i + 1);
		}
		
		boolean beanRef = attr.charAt(0) == '@';
		if (beanRef) {
			attr = attr.substring(1);
		}
		
		if (property == null) {
			property = FormatUtils.xmlToCamelCase(attr);
		}
		
		String value = element.getAttribute(attr); 
		if (StringUtils.hasText(value)) {
			if (beanRef) {
				pv.addPropertyValue(property, new RuntimeBeanReference(value));
			}
			else {
				pv.addPropertyValue(property, value);
			}
		}
	}

}
