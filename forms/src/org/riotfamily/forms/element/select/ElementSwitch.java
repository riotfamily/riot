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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.select;

import java.util.List;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.Container;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.NestedEditor;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;

public class ElementSwitch extends Container 
		implements Editor, NestedEditor, ChangeListener {
	
	private SelectBox selectBox = new SelectBox();

	private List<String> options = Generics.newArrayList();

	private Map<String, SwitchCase> cases = Generics.newHashMap();
	
	private SwitchCase activeCase;
	
	public ElementSwitch() {
		selectBox = new SelectBox();
		selectBox.setRequired(true);
		selectBox.setOptions(options);
		selectBox.addChangeListener(this);
		selectBox.setAppendLabel(true);
		addComponent(selectBox);
	}

	@Override
	protected void afterBindingSet() {
		selectBox.setLabelMessageKey(getEditorBinding().getProperty() + ".");
	}
	
	@Override
	public boolean isCompositeElement() {		
		return false;
	}
		
	public void addElement(Element element) {
		SwitchCase switchCase = (SwitchCase) element;
		switchCase.setVisible(false);
		switchCase.setSwitchBinding(getEditorBinding());
		String value = switchCase.getValue();
		cases.put(value, switchCase);
		options.add(value);
		selectBox.reset();
		super.addElement(element);
	}

	private void activateCase(Object value) {
		if (activeCase != null) {
			activeCase.deactivate();
		}
		if (value != null) {
			activeCase = cases.get(value);
			activeCase.activate();
		}
	}
	
	// ---------------------------------------------------------------------
	// Implementation of the ChangeListener interface
	// ---------------------------------------------------------------------
	
	public void valueChanged(ChangeEvent event) {
		activateCase(event.getNewValue());
	}
	
	// ---------------------------------------------------------------------
	// Implementation of the Editor interface
	// ---------------------------------------------------------------------
	
	public Object getValue() {
		activeCase.populateBackingObject();
		return selectBox.getValue();
	}

	public void setValue(Object value) {
		if (value == null) {
			value = options.get(0);
		}
		selectBox.setValue(value);
		activateCase(value);
		activeCase.initEditors();
	}
	
	// ---------------------------------------------------------------------
	// Implementation of the NestedEditor interface
	// ---------------------------------------------------------------------

	public Editor getEditor(String property) {
		int i = property.indexOf('.');
		String discriminator = property.substring(0, i);
		return cases.get(discriminator).getEditor(property.substring(i + 1));
	}

	public void setBackingObject(Object obj) {
		for (SwitchCase c : cases.values()) {
			c.setBackingObject(obj);
		}
	}

}
