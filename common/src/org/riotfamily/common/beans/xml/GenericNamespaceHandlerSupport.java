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

import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class GenericNamespaceHandlerSupport extends NamespaceHandlerSupport {

	protected void registerGenericParser(String elementName, Class beanClass) {
		registerBeanDefinitionParser(elementName, new GenericBeanDefinitionParser(beanClass));
	}

	protected void registerGenericParserAndDecorator(String elementName,
			Class beanClass, BeanDefinitionDecorator decorator) {

		registerGenericParser(elementName, beanClass);
		registerBeanDefinitionDecorator(elementName, decorator);
	}

	protected void registerParserAndDecorator(String elementName,
			BeanDefinitionParser parser, BeanDefinitionDecorator decorator) {

		registerBeanDefinitionParser(elementName, parser);
		registerBeanDefinitionDecorator(elementName, decorator);
	}

}
