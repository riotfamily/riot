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
package org.riotfamily.common.beans.xml;

import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.xml.XmlUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * BeanDefinitionDecorator that decorates child elements.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class ChildDecorator implements BeanDefinitionDecorator {

	private Map<String, BeanDefinitionDecorator> decorators = Generics.newHashMap();
	
	private BeanDefinitionDecorator defaultDecorator;

	/**
	 * Registers a decorator for child elements with the specified name.
	 */
	public ChildDecorator register(String elementName, BeanDefinitionDecorator decorator) {
		decorators.put(elementName, decorator);
		return this;
	}

	/**
	 * Sets a default decorator that is used for all unmatched child elements.
	 */
	public ChildDecorator setDefault(BeanDefinitionDecorator decorator) {
		this.defaultDecorator = decorator;
		return this;
	}
	
	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {

		for (Element el : XmlUtils.getChildElements((Element) node)) {
			BeanDefinitionDecorator decorator = decorators.get(XmlUtils.getLocalName(el));
			if (decorator == null) {
				decorator = defaultDecorator;
			}
			if (decorator != null) {
				decorator.decorate(el, definition, parserContext);
			}
		}
		return definition;
	}

}
