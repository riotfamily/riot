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
package org.riotfamily.forms.element.collection;

import java.util.Iterator;

import org.riotfamily.common.xml.XmlUtils;
import org.springframework.util.Assert;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlSequence extends ListEditor {

	private String name;

	private Node node;

	public void setName(String name) {
		this.name = name;
	}

	protected void afterBindingSet() {
		if (name == null) {
			name = getEditorBinding().getProperty();
		}
	}

	/**
	 *
	 */
	public void setValue(Object value) {
		if (value != null) {
			Assert.isInstanceOf(Node.class, value,
					"Value must implement the org.w3c.Node interface");

			node = (Node) value;
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node child = childNodes.item(i);
				ListItem item = addItem();
				item.getElement().setValue(child);
			}
		}
	}

	public Object getValue() {
		if (node != null) {
			XmlUtils.removeAllChildNodes(node);
		}
		else {
			node = XmlUtils.createDocument().createElement(name);
		}
		Iterator it = getItems().getElements().iterator();
		while (it.hasNext()) {
			ListItem item = (ListItem) it.next();
			if (item.getElement().getValue() != null) {
				Object value = item.getElement().getValue();
				Assert.isInstanceOf(Node.class, value,
						"Value must implement the org.w3c.Node interface");

				XmlUtils.importAndAppend((Node) value, node);
			}
		}
		return node;
	}

	protected void validate() {
	}

}
