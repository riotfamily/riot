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

import java.util.Collection;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ListItemDecorator implements BeanDefinitionDecorator {

	private String listPropertyName;

	public ListItemDecorator(String listPropertyName) {
		this.listPropertyName = listPropertyName;
	}

	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {

		BeanDefinition bd = definition.getBeanDefinition();
		MutablePropertyValues pvs = bd.getPropertyValues();
		Collection c = null;
		PropertyValue pv = pvs.getPropertyValue(listPropertyName);
		if (pv != null) {
			c = (Collection) pv.getValue();
		}
		if (c == null) {
			c = new ManagedList();
			pvs.addPropertyValue(listPropertyName, c);
		}
		c.add(parserContext.getDelegate().parsePropertySubElement((Element) node, bd));
		return definition;
	}
}