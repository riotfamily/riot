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

import org.riotfamily.common.beans.xml.GenericBeanDefinitionParser;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.website.template.PushUpTemplateController;
import org.riotfamily.website.template.TemplateController;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class TemplateDefinitionParser extends GenericBeanDefinitionParser {

	public TemplateDefinitionParser() {
		super(TemplateController.class);
		addReference("parent");
		setDecorate(false);
	}

	protected void postProcess(BeanDefinitionBuilder beanDefinition, Element element) {

		HashMap configuration = new HashMap();
		ArrayList pushUpSlots = null;

		Iterator it = DomUtils.getChildElementsByTagName(element, "insert").iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String slot = ele.getAttribute("slot");
			String url = ele.getAttribute("url");
			Integer pushUp = XmlUtils.getIntegerAttribute(ele, "push-up");
			if (pushUp != null) {
				if (pushUpSlots == null) {
					pushUpSlots = new ArrayList();
					beanDefinition.addPropertyValue("pushUpSlots", pushUpSlots);
					beanDefinition.getBeanDefinition().setBeanClass(
							PushUpTemplateController.class);
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
			beanDefinition.addPropertyValue("configuration", configuration);
		}
	}

}
