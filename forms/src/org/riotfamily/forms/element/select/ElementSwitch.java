/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.ui.Dimension;

public class ElementSwitch extends Container 
		implements Editor, NestedEditor, ChangeListener {
	
	private SelectBox selectBox = new SelectBox();

	private List<String> options = Generics.newArrayList();

	private Map<String, SwitchCase> cases = Generics.newHashMap();
	
	private SwitchCase activeCase;
	
	private String labelMessageKey;
	
	private boolean permanent = false;
	
	public ElementSwitch() {
		selectBox = new SelectBox();
		selectBox.setRequired(true);
		selectBox.setOptions(options);
		selectBox.addChangeListener(this);
		selectBox.setAppendLabel(true);
		selectBox.setStyleClass("element-switch");
		addComponent(selectBox);
	}

	@Override
	protected void afterBindingSet() {
		if (labelMessageKey == null) {
			labelMessageKey = getEditorBinding().getProperty() + ".";
		}
		selectBox.setLabelMessageKey(labelMessageKey);
	}
	
	@Override
	public boolean isCompositeElement() {		
		return false;
	}
	
	public void setLabelMessageKey(String labelMessageKey) {
		this.labelMessageKey = labelMessageKey;
	}
	
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
		
	@Override
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

	public Dimension getDimension() {
		Dimension d = new Dimension();
		for (SwitchCase c : cases.values()) {
			d.expand(c.getDimension());
		}
		return d.addHeight(selectBox.getDimension());
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
	
	@Override
	public void processRequest(FormRequest request) {
		activeCase.processRequest(request);
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
		for(SwitchCase c : cases.values()) {
			if (c != activeCase) {
				c.clear();
			}
		}
		activeCase.populateBackingObject();
		return selectBox.getValue();
	}

	public void setValue(Object value) {
		if (value == null) {
			value = options.get(0);
		}
		else if (permanent) {
			selectBox.setEnabled(false);
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

}
