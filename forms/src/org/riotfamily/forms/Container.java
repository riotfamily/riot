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
package org.riotfamily.forms;

import java.util.List;


/**
 * Composite element that notifies the form whenever an element is added or 
 * removed. This way elements can benefit from the framework's AJAX support 
 * without needing to know anything about. 
 * Refer to the {@link org.riotfamily.forms.element.collection.ListEditor} implementation
 * for an example.
 */
public class Container extends CompositeElement implements ContainerElement {

	/**
	 * Creates an empty container.
	 */
	public Container() {
	}

	public List<Element> getElements() {
		return getComponents();
	}

	public void addElement(Element element) {
		addComponent(element);
		if (getFormListener() != null) {
			getFormListener().elementAdded(element);
		}
	}

	/**
	 * Removes the given element from the container.
	 */
	public void removeElement(Element element) {
		removeComponent(element);
		getForm().unregisterElement(element);
		if (getFormListener() != null) {
			getFormListener().elementRemoved(element);
		}
	}
	
}