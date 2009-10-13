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
public class ChildDecorator implements BeanDefinitionDecorator, Cloneable {

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
	
	public ChildDecorator copy() {
		try {
			return (ChildDecorator) clone();
		} 
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
