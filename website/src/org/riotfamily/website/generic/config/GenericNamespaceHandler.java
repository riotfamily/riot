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
 *   Carsten Woelk [cwoelk at neteye dot de]
 *   
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.generic.config;

import java.util.ArrayList;

import org.riotfamily.common.beans.xml.DefinitionParserUtils;
import org.riotfamily.common.beans.xml.SimpleGenericBeanDefinitionParser;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.website.generic.GenericController;
import org.riotfamily.website.generic.GenericViewController;
import org.riotfamily.website.generic.model.ModelPostProcessor;
import org.riotfamily.website.generic.model.hibernate.CurrentDateResolver;
import org.riotfamily.website.generic.model.hibernate.CurrentLanguageResolver;
import org.riotfamily.website.generic.model.hibernate.CurrentLocaleResolver;
import org.riotfamily.website.generic.model.hibernate.DateParameterResolver;
import org.riotfamily.website.generic.model.hibernate.DefaultParameterResolver;
import org.riotfamily.website.generic.model.hibernate.HqlModelBuilder;
import org.riotfamily.website.generic.model.hibernate.PagedHqlModelBuilder;
import org.riotfamily.website.generic.model.hibernate.RiotPrincipalResolver;
import org.riotfamily.website.generic.model.hibernate.StringToPrimitiveResolver;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * NamespaceHandler that handles the <code>generic</code> namspace as
 * defined in <code>generic.xsd</code> which can be found in the same package.
 */
public class GenericNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		SimpleGenericBeanDefinitionParser simpleParser = new SimpleGenericBeanDefinitionParser();
		simpleParser.registerElement("view", GenericViewController.class);
		simpleParser.registerElement("hql", HqlModelBuilder.class);
		simpleParser.registerElement("paged-hql", PagedHqlModelBuilder.class);
		simpleParser.registerElement("attribute", DefaultParameterResolver.class);
		simpleParser.registerElement("current-date", CurrentDateResolver.class);
		simpleParser.registerElement("current-locale", CurrentLocaleResolver.class);
		simpleParser.registerElement("current-language", CurrentLanguageResolver.class);
		simpleParser.registerElement("riot-principal", RiotPrincipalResolver.class);
		simpleParser.registerElement("string-to-primitive", StringToPrimitiveResolver.class);
		simpleParser.registerElement("date", DateParameterResolver.class);

		registerBeanDefinitionParser("view", simpleParser);
		registerBeanDefinitionParser("controller", new ControllerBeanParser());
		registerBeanDefinitionParser("hql", simpleParser);
		registerBeanDefinitionParser("paged-hql", simpleParser);
		registerBeanDefinitionParser("attribute", simpleParser);
		registerBeanDefinitionParser("current-date", simpleParser);
		registerBeanDefinitionParser("current-locale", simpleParser);
		registerBeanDefinitionParser("current-language", simpleParser);
		registerBeanDefinitionParser("riot-principal", simpleParser);
		registerBeanDefinitionParser("string-to-primitive", simpleParser);
		registerBeanDefinitionParser("date", simpleParser);
	}



	private static class ControllerBeanParser extends SimpleGenericBeanDefinitionParser {

		private static final String[] CONTROLLER_ATTRIBUTES = {
			"view-name", "content-type", "add-uri-to-cache-key",
			"cacheable", "@model-builder"
		};
		private static final String MODEL_BUILDER_PROPERTY = "modelBuilder";
		private static final String POST_PROCESSORS_PROPERTY = "postProcessors";

		protected Class getBeanClass(Element element) {
			return GenericController.class;
		}

		protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
			// Properties
			DefinitionParserUtils.addProperties(builder, element, CONTROLLER_ATTRIBUTES);

			// First child needs to be our ModelBuilder
			Element modelBuilderElement = XmlUtils.getFirstChildElement(element);
			builder.addPropertyValue(MODEL_BUILDER_PROPERTY,
					parserContext.getDelegate().parsePropertySubElement(
							modelBuilderElement, builder.getBeanDefinition()));

			ArrayList modelPostProcessors = new ArrayList();
			Node sibling = modelBuilderElement.getNextSibling();
			while ((sibling = sibling.getNextSibling()) != null) {
				if (sibling instanceof Element) {
					Element modelPostProcessorElement = (Element) sibling;
					modelPostProcessors.add(
							parserContext.getDelegate().parsePropertySubElement(
									modelPostProcessorElement, builder.getBeanDefinition()));
				}
			}
			if(!modelPostProcessors.isEmpty()) {
				builder.addPropertyValue(POST_PROCESSORS_PROPERTY, modelPostProcessors.toArray(new ModelPostProcessor[] {}));
			}
		}

	}

}
