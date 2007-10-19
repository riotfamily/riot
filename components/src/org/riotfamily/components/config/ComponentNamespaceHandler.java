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

import org.riotfamily.common.beans.xml.AbstractGenericBeanDefinitionParser;
import org.riotfamily.common.beans.xml.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.xml.NestedListDecorator;
import org.riotfamily.components.config.component.IncludeComponent;
import org.riotfamily.components.config.component.StaticComponent;
import org.riotfamily.components.config.component.ViewComponent;
import org.riotfamily.components.property.DefaultValuePropertyProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * NamespaceHandler that handles the <code>component</code>
 * namespace as defined in <code>component.xsd</code> which can be found in
 * the same package.
 */
public class ComponentNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("static-component", StaticComponent.class);
		register("view-component", ViewComponent.class);
		register("include-component", IncludeComponent.class);

		BeanDefinitionDecorator addPropertyProcessor =
				new NestedListDecorator("propertyProcessors");

		registerSpringBeanDefinitionParser("property-processor",
				addPropertyProcessor);

		register("defaults", new DefaultValueParser(), addPropertyProcessor);
	}

	private static class DefaultValueParser extends
			AbstractGenericBeanDefinitionParser {

		public DefaultValueParser() {
			super(DefaultValuePropertyProcessor.class);
		}

		protected void doParse(Element element, ParserContext parserContext,
				BeanDefinitionBuilder builder) {

			builder.addPropertyValue("values", parserContext.getDelegate()
					.parsePropsElement(element));
		}

	}
}
