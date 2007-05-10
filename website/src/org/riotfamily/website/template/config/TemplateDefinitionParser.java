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

import org.riotfamily.common.beans.xml.DefinitionParserUtils;
import org.riotfamily.common.xml.XmlUtils;
import org.riotfamily.website.template.PushUpTemplateController;
import org.riotfamily.website.template.TemplateController;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class TemplateDefinitionParser implements BeanDefinitionParser {

	private static final String ID_ATTRIBUTE = "id";

	private static final String NAME_ATTRIBUTE = "name";

	private static final String PARENT_ATTRIBUTE = "parent";

	private static final String VIEW_NAME_ATTRIBUTE = "view-name";

	private static final String SESSION_ATTRIBUTE = "session";

	private static final String INSERT_TAG = "insert";

	private static final String REMOVE_TAG = "remove";

	private static final String SLOT_ATTRIBUTE = "slot";

	private static final String URL_ATTRIBUTE = "url";

	private static final String PUSH_UP_ATTRIBUTE = "push-up";

	private static final String PUSH_UP_SLOTS_PROPERTY = "pushUpSlots";

	private static final String CONFIGURATION_PROPERTY = "configuration";

	public BeanDefinition parse(Element element, ParserContext parserContext) {

		RootBeanDefinition definition = new RootBeanDefinition();
		definition.setBeanClass(TemplateController.class);

		DefinitionParserUtils.registerBeanDefinition(definition, element,
				ID_ATTRIBUTE, NAME_ATTRIBUTE, parserContext);

		MutablePropertyValues pv = new MutablePropertyValues();

		DefinitionParserUtils.addReference(pv, element, PARENT_ATTRIBUTE);
		DefinitionParserUtils.addString(pv, element, VIEW_NAME_ATTRIBUTE);
		DefinitionParserUtils.addString(pv, element, SESSION_ATTRIBUTE);

		HashMap configuration = new HashMap();
		ArrayList pushUpSlots = null;

		Iterator it = DomUtils.getChildElementsByTagName(
				element, INSERT_TAG).iterator();

		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String slot = ele.getAttribute(SLOT_ATTRIBUTE);
			String url = ele.getAttribute(URL_ATTRIBUTE);
			Integer pushUp = XmlUtils.getIntegerAttribute(ele, PUSH_UP_ATTRIBUTE);
			if (pushUp != null) {
				if (pushUpSlots == null) {
					pushUpSlots = new ArrayList();
					pv.addPropertyValue(PUSH_UP_SLOTS_PROPERTY, pushUpSlots);
					definition.setBeanClass(PushUpTemplateController.class);
				}
				pushUpSlots.add(slot);
			}
			configuration.put(slot, url);
		}

		it = DomUtils.getChildElementsByTagName(
				element, REMOVE_TAG).iterator();

		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String slot = ele.getAttribute(SLOT_ATTRIBUTE);
			configuration.put(slot, null);
		}

		if (!configuration.isEmpty()) {
			pv.addPropertyValue(CONFIGURATION_PROPERTY, configuration);
		}

		definition.setPropertyValues(pv);

		return definition;
	}

}
