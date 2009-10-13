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
package org.riotfamily.forms;

import java.util.ArrayList;
import java.util.List;

import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;


/**
 * Abstract base class for editor elements.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractEditorBase extends AbstractElement {

	private String paramName;

	private EditorBinding binding;
	
	private String fieldName;
	
	private List<ChangeListener> listeners;
	
	public final void setEditorBinding(EditorBinding binding) {
		this.binding = binding;
		afterBindingSet();
	}	
	
	protected void afterBindingSet() {
	}
	
	public EditorBinding getEditorBinding() {
		return binding;
	}
	
	public String getParamName() {
		if (paramName == null) {
			paramName = getForm().createUniqueParameterName();
		}
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		if (fieldName != null) {
			return fieldName;
		}
		if (binding != null) {
			return binding.getPropertyPath();
		}
		return "unbound-" + getId();
	}
	
	public String getLabel() {
		String label = super.getLabel();
		if (label == null && binding != null) {
			label = MessageUtils.getLabel(this, binding);
			super.setLabel(label);
		}		
		return label;
	}
		
	public String getHint() {
		String hint = super.getHint();
		if (hint == null && binding != null) {
			hint = MessageUtils.getHint(this, binding);
			super.setHint(hint);
		}		
		return hint;
	}
	
	public final void addChangeListener(ChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ChangeListener>();
		}
		listeners.add(listener);
	}

	protected final void fireChangeEvent(Object newValue, Object oldValue) {
		if (listeners != null) {
			ChangeEvent event = new ChangeEvent(this, newValue, oldValue);
			for (ChangeListener listener : listeners) {
				listener.valueChanged(event);
			}
		}
	}
	
	protected boolean hasListeners() {
		return listeners != null;
	}
}
