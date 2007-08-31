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
package org.riotfamily.website.template.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.beans.xml.GenericBeanDefinitionParser;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.website.template.PushUpTemplateController;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class TemplateDefinitionParser extends GenericBeanDefinitionParser {

	private String prefix = "/inc";
	
	private String suffix = ".html";
	
	public TemplateDefinitionParser() {
		super(PushUpTemplateController.class);
		addReference("parent");
		setDecorate(false);
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	protected void postProcess(BeanDefinitionBuilder builder, 
			ParserContext parserContext, Element element) {

		HashMap configuration = new HashMap();
		ArrayList pushUpSlots = null;
		
		Iterator it = DomUtils.getChildElementsByTagName(element, "insert").iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String slot = XmlUtils.getAttribute(ele, "slot");
			Object url = XmlUtils.getAttribute(ele, "url");
			if (url == null) {
				String templateId = XmlUtils.getAttribute(element, "id");
				List handlersElements = XmlUtils.getChildElements(ele);
				if (handlersElements.size() == 1) {
					url = getHandlerUrl(templateId, slot, 
							(Element) handlersElements.get(0), 
							parserContext);
				}
				else if (handlersElements.size() > 1) {
					url = getHandlerUrls(templateId, slot, 
							handlersElements, parserContext);
				}
			}
			Integer pushUp = XmlUtils.getIntegerAttribute(ele, "push-up");
			if (pushUp != null) {
				if (pushUpSlots == null) {
					pushUpSlots = new ArrayList();
					builder.addPropertyValue("pushUpSlots", pushUpSlots);
				}
				pushUpSlots.add(slot);
			}
			configuration.put(slot, url);
		}

		it = DomUtils.getChildElementsByTagName(element, "remove").iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String slot = ele.getAttribute("slot");
			configuration.put(slot, null);
		}

		if (!configuration.isEmpty()) {
			builder.addPropertyValue("configuration", configuration);
		}
	}
	
	private List getHandlerUrls(String templateId, String slot, 
			List handlerElements, ParserContext parserContext) {
		
		ArrayList urls = new ArrayList();
		int i = 0;
		Iterator it = handlerElements.iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			urls.add(getHandlerUrl(templateId, slot + i++, ele, parserContext));
		}
		return urls;
	}
	
	private String getHandlerUrl(String templateId, String slot,
			Element handlerElement, ParserContext parserContext) {
		
		BeanDefinition bd;
		BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
		
		if (delegate.isDefaultNamespace(handlerElement.getNamespaceURI())) {
			bd = delegate.parseBeanDefinitionElement(handlerElement, null, null);
		}
		else {
			bd = delegate.parseCustomElement(handlerElement);
		}
		
		String beanName = delegate.getReaderContext()
				.generateBeanName(bd).replace('#', '-');
		
		if (templateId == null) {
			templateId = beanName;
		}
		String url = prefix  + "/" + templateId + "/" + slot + suffix; 
		String[] aliases = new String[] {url};
		
		BeanDefinitionHolder bdHolder = new BeanDefinitionHolder(bd, beanName, aliases);
		BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, parserContext.getRegistry());
		return url;
	}

}
