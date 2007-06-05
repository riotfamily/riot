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
package org.riotfamily.pages.setup.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.riotfamily.common.beans.xml.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.xml.NestedListDecorator;
import org.riotfamily.pages.setup.PageDefinition;
import org.riotfamily.pages.setup.PageSetupBean;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * NamespaceHandler that handles the <code>page</code> namspace as
 * defined in <code>page.xsd</code> which can be found in the same package.
 */
public class PageNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("setup", PageSetupBean.class)
				.addTranslation("tx", "transactionManager")
				.addTranslation("dao", "pageDao")
				.addReference("dao")
				.addReference("locales")
				.addReference("tx");

		register("page", PageDefinition.class,
				new NestedListDecorator("definitions"))
				.addTranslation("system", "systemNode");

		registerBeanDefinitionDecorator("props", new PropsDecorator());
	}

	private static class PropsDecorator implements BeanDefinitionDecorator {

		private static final String LOCALE_ATTRIBUTE = "locale";
		private static final String LOCALIZED_PROPS_NAME = "localizedProps";
		private static final String GLOBAL_PROPS_NAME = "globalProps";

		public BeanDefinitionHolder decorate(Node node,
				BeanDefinitionHolder definition, ParserContext parserContext) {

			BeanDefinition bd = definition.getBeanDefinition();
			MutablePropertyValues pvs = bd.getPropertyValues();

			Element element = (Element) node;
			Properties props = parserContext.getDelegate().parsePropsElement(element);

			String locale = element.getAttribute(LOCALE_ATTRIBUTE);
			if(StringUtils.hasText(locale)) {
				Map map = null;
				PropertyValue pv = pvs.getPropertyValue(LOCALIZED_PROPS_NAME);
				if (pv != null) {
					map = (Map) pv.getValue();
				}
				if (map == null) {
					map = new HashMap();
					pvs.addPropertyValue(LOCALIZED_PROPS_NAME, map);
				}
				map.put(locale, props);
			}
			else {
				pvs.addPropertyValue(GLOBAL_PROPS_NAME, props);
			}

			return definition;
		}
	}
}
