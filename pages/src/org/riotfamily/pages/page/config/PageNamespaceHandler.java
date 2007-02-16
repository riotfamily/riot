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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.page.config;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.beans.xml.DefinitionParserUtils;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.pages.page.PersistentPage;
import org.riotfamily.pages.page.support.SitemapSetupBean;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * NamespaceHandler that handles the <code>page</code> namspace as
 * defined in <code>page.xsd</code> which can be found in the same package.
 */
public class PageNamespaceHandler implements NamespaceHandler {
	static final Log log =
		LogFactory.getLog(PageNamespaceHandler.class);

	private static final String SETUP = "setup";
	
	private static final String[] SETUP_ATTRIBUTES = {
		"pageDao=@dao"
	};
	
	private static final String PAGE = "page";
	
	private static final String[] PAGE_ATTRIBUTES = {
		"path-component", "title", "systemPage=system", 
		"controllerName=controller", "published", "hidden"
	};
	
	private static final String PAGES_PROPERTY = "pages";
	
	private static final String CHILD_PAGES_PROPERTY = "childPages";

	private static final String CLASS_PROPERTY = "class";
	
	public void init() {
	}
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String name = element.getLocalName();
		if (SETUP.equals(name)) {
			return parseSetup(element, parserContext);
		}
		else if (PAGE.equals(name)) {
			return parsePage(element, parserContext);
		}
		throw new IllegalArgumentException("Element not supported: " + name);
	}
	
	protected BeanDefinition parseSetup(Element element, 
			ParserContext parserContext) {
		
		RootBeanDefinition definition = new RootBeanDefinition();
		definition.setBeanClass(SitemapSetupBean.class);
		
		MutablePropertyValues pv = new MutablePropertyValues();
		definition.setPropertyValues(pv);
		DefinitionParserUtils.addProperties(pv, element, SETUP_ATTRIBUTES);
		List children = XmlUtils.getChildElementsByRegex(element, PAGE);
		if (children != null && !children.isEmpty()) {
			ManagedList pages = new ManagedList();
			pv.addPropertyValue(PAGES_PROPERTY, pages);
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element ele = (Element) it.next();
				BeanDefinition def = parsePage(ele, parserContext);
				RuntimeBeanReference ref = DefinitionParserUtils
						.registerAnonymousBeanDefinition(def, parserContext);
				
				pages.add(ref);
			}
		}
		DefinitionParserUtils.registerAnonymousBeanDefinition(
				definition, parserContext);
		
		return definition;
	}
	
	protected Class getCustomPageClass(Element element,
		ParserContext parserContext) {
		
		Class clazz = null;
		
		String className = element.getAttribute(CLASS_PROPERTY);
		if (StringUtils.hasText(className)) {
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Cannot create Page of type ["
					+ className + "]", e);
			}
		}
		
		return clazz;
	}
	
	protected BeanDefinition parsePage(Element element, 
			ParserContext parserContext) {
		
		RootBeanDefinition definition = new RootBeanDefinition();
		Class clazz = getCustomPageClass(element, parserContext);
		if (clazz == null) {
			clazz = PersistentPage.class;
		} else {
			Assert.isAssignable(PersistentPage.class, clazz);
		}
		definition.setBeanClass(clazz);
		
		MutablePropertyValues pv = new MutablePropertyValues();
		definition.setPropertyValues(pv);
		DefinitionParserUtils.addProperties(pv, element, PAGE_ATTRIBUTES);
		List children = XmlUtils.getChildElementsByRegex(element, PAGE);
		if (children != null && !children.isEmpty()) {
			ManagedList childPages = new ManagedList();
			pv.addPropertyValue(CHILD_PAGES_PROPERTY, childPages);
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element ele = (Element) it.next();
				BeanDefinition def = parsePage(ele, parserContext);
				RuntimeBeanReference ref = DefinitionParserUtils
						.registerAnonymousBeanDefinition(def, parserContext);
				
				childPages.add(ref);
			}
		}
		return definition;
	}
	
	public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder 
			holder, ParserContext parserContext) {
		
		throw new UnsupportedOperationException(
				"Bean decoration is not supported.");
	}

}
