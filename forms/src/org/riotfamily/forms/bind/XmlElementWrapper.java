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
package org.riotfamily.forms.bind;

import org.riotfamily.common.beans.ObjectWrapper;
import org.riotfamily.common.xml.XmlUtils;
import org.springframework.beans.AbstractPropertyAccessor;
import org.springframework.beans.BeansException;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlElementWrapper extends AbstractPropertyAccessor
		implements ObjectWrapper {

	private static final String ATTRIBUTE_PREFIX = "@";

	private static final String TEXT_VALUE = "text()";

	public Element element;

	private String elementName;


	public XmlElementWrapper(String elementName) {
		this.elementName = elementName;
	}

	public Class getObjectClass() {
		return Element.class;
	}

	public void setObject(Object object) {
		if (object != null) {
			element = (Element) object;
		}
		else {
			element = XmlUtils.createDocument().createElement(elementName);
		}
	}

	public Object getObject() {
		return element;
	}

	public Object getPropertyValue(String name) throws BeansException {
		if (element == null) {
			return null;
		}
		if (name.startsWith(ATTRIBUTE_PREFIX)) {
			return element.getAttribute(name.substring(1));
		}
		if (name.equals(TEXT_VALUE)) {
			return DomUtils.getTextValue(element);
		}
		Element child = XmlUtils.getFirstChildByTagName(element, name);
		return child != null ? DomUtils.getTextValue(child) : null;
	}

	public void setPropertyValue(String name, Object value) throws BeansException {
		if (element == null) {
			element = XmlUtils.createDocument().createElement(elementName);
		}
		if (name.startsWith(ATTRIBUTE_PREFIX)) {
			if (value != null) {
				element.setAttribute(name.substring(1), value.toString());
			}
			else {
				element.removeAttribute(name);
			}
		}
		else if (name.equals(TEXT_VALUE)) {
			XmlUtils.setTextValue(element, value.toString());
		}
		if (value instanceof Element) {
			Element child = XmlUtils.getFirstChildByTagName(element, name);
			XmlUtils.removeNode(child);
			child = (Element) value;
			Node node = element.getOwnerDocument().importNode(child, true);
			element.appendChild(node);
		}
		else {
			Element child = XmlUtils.getFirstChildByTagName(element, name);
			if (value != null) {
				if (child == null) {
					child = element.getOwnerDocument().createElement(name);
					element.appendChild(child);
				}
				XmlUtils.setTextValue(child, (String) value);
			}
			else {
				XmlUtils.removeNode(child);
			}
		}
	}

	public boolean isReadableProperty(String propertyName) throws BeansException {
		return true;
	}

	public boolean isWritableProperty(String propertyName) throws BeansException {
		return true;
	}

}
