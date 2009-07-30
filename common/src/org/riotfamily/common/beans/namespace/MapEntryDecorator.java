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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
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