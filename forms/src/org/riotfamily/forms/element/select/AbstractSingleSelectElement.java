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
package org.riotfamily.forms.element.select;

import java.util.Collection;

import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.request.FormRequest;


/**
 * Abstract superclass for elements that let the user choose from a set of
 * options like selectboxes or radio button groups.
 */
public abstract class AbstractSingleSelectElement 
		extends AbstractSelectElement {
	
	private Object selectedValue;
	
	public final void setValue(Object value) {
		this.selectedValue = value;
	}
	
	public Object getValue() {
		return selectedValue;
	}

	protected boolean hasSelection() {
		return selectedValue != null;
	}
	
	public boolean isSelected(OptionItem option) {
		return hasSelection() && selectedValue.equals(option.getValue());
	}

	/**
	 * @see org.riotfamily.forms.AbstractElement#processRequest
	 */
	public void processRequest(FormRequest request) {
		updateSelection(request.getParameter(getParamName()));
	}
	
	public int getSelectedIndex() {
		if (hasSelection()) {
			for (int i = 0; i < getOptionItems().size(); i++) {
				OptionItem option = getOptionItems().get(i);
				if (selectedValue.equals(option.getValue())) {
					return i;
				}
			}
		}
		return -1;
	}
	
	protected void updateSelection(Collection<?> optionValues) {
		if (optionValues != null && selectedValue != null) {
			for (Object item : optionValues) {
				Object value = getOptionValue(item);
				if (selectedValue.equals(value)) {
					selectedValue = value;
					return;
				}
			}
		}
		selectedValue = null;
	}
	
	private void updateSelection(String index) {
		int i = -1;
		if (index != null) {
			i = Integer.parseInt(index);
		}
		if (i >= 0) {
			OptionItem option = getOptionItems().get(i);
			selectedValue = option.getValue();
		}
		else {
			selectedValue = null;	
		}
		validate();
	}
	
	public void handleJavaScriptEvent(JavaScriptEvent event) {
		if (event.getType() == JavaScriptEvent.ON_CHANGE) {
			Object oldValue = selectedValue;
			updateSelection(event.getValue());
			fireChangeEvent(selectedValue, oldValue);
		}
	}

}