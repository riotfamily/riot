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
package org.riotfamily.forms.element.core;

import java.util.List;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.element.ContainerElement;
import org.riotfamily.forms.element.support.Container;
import org.riotfamily.forms.element.support.TemplateElement;
import org.riotfamily.forms.i18n.MessageUtils;


/**
 * Element that visually groups other elements.
 */
public class ElementGroup extends TemplateElement implements ContainerElement {

	private Container container = new Container();

	private String labelKey;
	
	private boolean labelItems = true;

	public ElementGroup() {
		super("group");
		addComponent("elements", container);
	}

	public List getElements() {
		return container.getElements();
	}
	
	public void addElement(Element element) {
		container.addElement(element);
	}
	
	public void removeElement(Element element) {
		container.removeElement(element);
	}

	public void setLabelKey(String key) {
		labelKey = key;
	}	

	public boolean isLabelItems() {
		return labelItems;
	}

	public void setLabelItems(boolean labelItems) {
		this.labelItems = labelItems;
	}

	public String getLabel() {
		if (labelKey == null) {
			return "";
		}
		return MessageUtils.getMessage(this, labelKey);
	}
}
