/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.beans.override;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.riotfamily.common.beans.namespace.GenericBeanDefinitionParser;
import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;
import org.riotfamily.common.util.XmlUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class OverrideNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("if-present", new ConditionalParser());
		registerBeanDefinitionParser("properties", new PropertyOverrideParser());
		registerBeanDefinitionParser("put", new MapMergeParser());
		registerBeanDefinitionParser("add", new ListMergeParser());
		registerBeanDefinitionParser("bean", new BeanOverrideParser());
	}
	
	private static class ConditionalParser implements BeanDefinitionParser {

		public BeanDefinition parse(Element element, ParserContext parserContext) {
			BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
			BeanDefinitionRegistry registry = parserContext.getRegistry();
			String ref = element.getAttribute("ref");
			if (registry.containsBeanDefinition(ref)) {
				for (Element child : XmlUtils.getChildElements(element)) {
					if (delegate.isDefaultNamespace(child.getNamespaceURI())) {
						BeanDefinitionHolder bdh = delegate.parseBeanDefinitionElement(child);
						String id = bdh.getBeanName();
						if (id != null) {
							registry.registerBeanDefinition(id, bdh.getBeanDefinition());
						}
					}
					else {
						delegate.parseCustomElement(child);
					}
				}
			}
			return null;
		}
		
	}
	
	private static class PropertyOverrideParser extends GenericBeanDefinitionParser {
		
		public PropertyOverrideParser() {
			super(PropertyOverrideProcessor.class);
		}
		
		@Override
		protected void postProcess(RootBeanDefinition bean, 
				ParserContext parserContext, Element element) {
			
			BeanDefinition bd = new RootBeanDefinition();
			parserContext.getDelegate().parsePropertyElements(element, bd);
			bean.getPropertyValues().add("propertyValues", bd.getPropertyValues());
		}
	}
	
	private static class MapMergeParser extends GenericBeanDefinitionParser {
		
		public MapMergeParser() {
			super(MapMergeProcessor.class);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		protected void postProcess(RootBeanDefinition bean, 
				ParserContext parserContext, Element element) {
			
			Map entries = parserContext.getDelegate().parseMapElement(element, bean);
			
			// The parsed Map is a ManagedMap. We put the values into a
			// HashMap so that the reference resolution is deferred until
			// the actual target bean is initialized.
			bean.getPropertyValues().add("entries", new HashMap(entries));
		}
	}
	
	private static class ListMergeParser extends GenericBeanDefinitionParser {
		
		public ListMergeParser() {
			super(ListMergeProcessor.class);
		}
		
		@Override
		protected void postProcess(RootBeanDefinition bean, 
				ParserContext parserContext, Element element) {
			
			List<?> values = parserContext.getDelegate().parseListElement(element, bean);
			
			// The parsed List is a ManagedList. We put the values into an
			// ArrayList so that the reference resolution is deferred until
			// the actual target bean is initialized.
			bean.getPropertyValues().add("values", new ArrayList<Object>(values));
		}
	}

	private static class BeanOverrideParser extends GenericBeanDefinitionParser {
		
		public BeanOverrideParser() {
			super(BeanOverrideProcessor.class);
		}
		
		@Override
		protected boolean isEligibleAttribute(String attributeName, 
				ParserContext parserContext) {
			
			return attributeName.equals("ref") 
					|| attributeName.equals("merge")
					|| attributeName.equals("order");
		}
		
		@Override
		protected void postProcess(RootBeanDefinition bean, 
				ParserContext parserContext, Element element) {
			
			BeanReplacement replacement = new BeanReplacement(
					parserContext.getDelegate().parseBeanDefinitionElement(
					element, null, bean));
			
			bean.getPropertyValues().add("beanReplacement", replacement).setConverted();
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
