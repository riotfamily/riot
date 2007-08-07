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
package org.riotfamily.common.beans.override;

import org.riotfamily.common.beans.xml.GenericBeanDefinitionParser;
import org.riotfamily.common.beans.xml.GenericNamespaceHandlerSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class OverrideNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("properties", new PropertyOverrideParser());
		registerBeanDefinitionParser("bean", new BeanReplacementParser());
	}
	
	private static class PropertyOverrideParser extends GenericBeanDefinitionParser {
		
		public PropertyOverrideParser() {
			super(PropertyOverrideProcessor.class);
		}
		
		protected void postProcess(BeanDefinitionBuilder beanDefinition, 
				ParserContext parserContext, Element element) {
			
			BeanDefinition bd = new RootBeanDefinition();
			parserContext.getDelegate().parsePropertyElements(element, bd);
			beanDefinition.addPropertyValue("propertyValues", bd.getPropertyValues());
		}
	}
	
	private static class BeanReplacementParser extends GenericBeanDefinitionParser {
		
		public BeanReplacementParser() {
			super(BeanReplacementProcessor.class);
		}
		
		protected boolean isEligibleAttribute(String attributeName) {
			return attributeName.equals("ref");
		}
		
		protected void postProcess(BeanDefinitionBuilder builder, 
				ParserContext parserContext, Element element) {
			
			BeanReplacement replacement = new BeanReplacement(
					parserContext.getDelegate().parseBeanDefinitionElement(
					element, null, builder.getBeanDefinition()));
			
			builder.addPropertyValue("beanReplacement", replacement);
			builder.getBeanDefinition().getPropertyValues().setConverted();
		}
	}
	
	static class BeanReplacement {
		
		private BeanDefinition beanDefinition;

		public BeanReplacement(BeanDefinition beanDefinition) {
			this.beanDefinition = beanDefinition;
		}

		public BeanDefinition getBeanDefinition() {
			return this.beanDefinition;
		}
		
	}
	
}
