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

import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.xml.XmlUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Node;

/**
 * BeanDefinitionDecorator that delegates to different delegates, depending
 * on the local name of the parent element.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class ContextualBeanDecorator implements BeanDefinitionDecorator {

	private Map<String,BeanDefinitionDecorator> decorators = Generics.newHashMap();

	public ContextualBeanDecorator register(String parentNodeName, BeanDefinitionDecorator decorator) {
		decorators.put(parentNodeName, decorator);
		return this;
	}
	
	public BeanDefinitionHolder decorate(Node node,
			BeanDefinitionHolder definition, ParserContext parserContext) {
		
		String parentName = XmlUtils.getLocalName(node.getParentNode());
		BeanDefinitionDecorator decorator = decorators.get(parentName);
		
		if (decorator == null) {
			XmlReaderContext ctx = parserContext.getReaderContext();
			ctx.error("No decorator registered for type '" 
					+ parentName + "'", ctx.extractSource(node));
		}
		return decorator.decorate(node, definition, parserContext);
	}

}
