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
package org.riotfamily.common.beans.namespace;

import java.util.List;

import org.riotfamily.common.xml.XmlUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * BeanDefinitionDecorator that calls 
 * {@link BeanDefinitionParserDelegate#parseListElement(Element, BeanDefinition)
 * delegate.parseListElement()} and sets the resulting list as property. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class ListDecorator implements BeanDefinitionDecorator {

	private String propertyName;

	public ListDecorator() {
	}
	
	public ListDecorator(String propertyName) {
		this.propertyName = propertyName;
	}

	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {

		BeanDefinition bd = definition.getBeanDefinition();
		List<?> list = parserContext.getDelegate().parseListElement((Element) node, bd);
		bd.getPropertyValues().addPropertyValue(getPropertyName(node), list);
		return definition;
	}

	private String getPropertyName(Node node) {
		if (propertyName != null) {
			return propertyName;
		}
		return XmlUtils.getLocalName(node);
	}
}