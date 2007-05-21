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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.beans.xml.DefinitionParserUtils;
import org.riotfamily.pages.setup.PageDefinition;
import org.riotfamily.pages.setup.PageSetupBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * NamespaceHandler that handles the <code>page</code> namspace as
 * defined in <code>page.xsd</code> which can be found in the same package.
 */
public class PageNamespaceHandler extends NamespaceHandlerSupport {
	static final Log log =
		LogFactory.getLog(PageNamespaceHandler.class);

	public void init() {
		registerBeanDefinitionParser("setup", new PageSetupBeanParser());
		registerBeanDefinitionParser("page", new PageDefinitionBeanParser());
	}

	private static class PageSetupBeanParser extends AbstractSingleBeanDefinitionParser {
		private static final String SETUP = "setup";
		private static final String[] PAGE_SETUP_ATTRIBUTES = {
			"pageDao=@dao",
			"transactionManager=@tx",
			"@locales"
		};
		private static final String PAGE_DEFINITIONS_PROPERTY = "definitions";

		protected Class getBeanClass(Element element) {
			return PageSetupBean.class;
		}

		protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
			String id = super.resolveId(element, definition, parserContext);
			if (!StringUtils.hasText(id)) {
				BeanDefinitionRegistry registry = parserContext.getRegistry();
				id = DefinitionParserUtils.generateBeanName(definition, registry, false);
			}
			return id;
		}

		protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
			String name = element.getLocalName();
			if (!SETUP.equals(name)) {
				throw new IllegalArgumentException("Element not supported: " + name);
			}

			// Properties
			DefinitionParserUtils.addProperties(builder, element, PAGE_SETUP_ATTRIBUTES);

			// Children
			List pages = parserContext.getDelegate().parseListElement(element, builder.getBeanDefinition());
			builder.addPropertyValue(PAGE_DEFINITIONS_PROPERTY, pages);
		}
	}

	private static class PageDefinitionBeanParser extends AbstractSingleBeanDefinitionParser {

		private static final String PAGE = "page";
		private static final String[] PAGE_DEFINITION_ATTRIBUTES = {
			"path-component", "handler-name", "child-handler-name",
			"hidden", "publish", "systemNode=system", "folder"
		};
		private static final String PAGE_DEFINITION_CHILDREN_PROPERTY = "children";

		protected Class getBeanClass(Element element) {
			return PageDefinition.class;
		}

		protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
			String name = element.getLocalName();
			if (!PAGE.equals(name)) {
				throw new IllegalArgumentException("Element not supported: " + name);
			}

			// Properties
			DefinitionParserUtils.addProperties(builder, element, PAGE_DEFINITION_ATTRIBUTES);

			// Children
			List pages = parserContext.getDelegate().parseListElement(element, builder.getBeanDefinition());
			builder.addPropertyValue(PAGE_DEFINITION_CHILDREN_PROPERTY, pages);
		}

	}

}
