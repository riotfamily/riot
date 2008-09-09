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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.select;

import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.AbstractEditorBinder;
import org.riotfamily.forms.BeanEditor;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.EditorBinding;
import org.riotfamily.forms.FormErrors;
import org.riotfamily.forms.element.ElementGroup;
import org.springframework.validation.FieldError;

public class SwitchCase extends ElementGroup implements BeanEditor {

	private String value;
	
	private String propertyPath;
	
	private DelegatingEditorBinder editorBinder = new DelegatingEditorBinder();

	private List<FieldError> errors = Generics.newArrayList();
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setSwitchBinding(EditorBinding binding) {
		editorBinder.setDelegate(binding.getEditorBinder());
		propertyPath = binding.getPropertyPath() + '.' + value;
	}
	
	public void initEditors() {
		editorBinder.initEditors();
	}
	
	public void populateBackingObject() {
		editorBinder.populateBackingObject();
	}
	
	public void activate() {
		FormErrors formErrors = getForm().getErrors();
		for (FieldError error : errors) {
			formErrors.addError(error);
		}
		setVisible(true);
	}
	
	public void deactivate() {
		FormErrors formErrors = getForm().getErrors();
		for (FieldError error : formErrors.getFieldErrors()) {
			if (error.getField().startsWith(propertyPath)) {
				errors.add(error);
				formErrors.removeError(error);
			}
		}
		setVisible(false);
	}
	
	// ------------------------------------------------------------------
	// Implementation of the BeanEditor interface 
	// ------------------------------------------------------------------
	
	public void bind(Editor editor, String property) {
		editorBinder.bind(editor, property);
	}

	public Editor getEditor(String property) {
		return editorBinder.getEditor(property);
	}

	public void setBeanClass(Class<?> beanClass) {
	}

	public void setBackingObject(Object obj) {
		editorBinder.setBackingObject(obj);
	}

	private class DelegatingEditorBinder extends AbstractEditorBinder {

		private EditorBinder delegate;
	
		public void setDelegate(EditorBinder delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public Class<?> getPropertyType(String path) {
			return delegate.getPropertyType(path);
		}

		public Object getBackingObject() {
			return delegate.getBackingObject();
		}

		public Class<?> getBeanClass() {
			return delegate.getBeanClass();
		}

		public Object getPropertyValue(String property) {
			return delegate.getPropertyValue(property);
		}

		public boolean isEditingExistingBean() {
			return delegate.isEditingExistingBean();
		}

		public void setPropertyValue(String property, Object value) {
			delegate.setPropertyValue(property, value);
		}
		
		@Override
		protected String getPropertyPath(Editor editor, String property) {
			return propertyPath + '.' + property;
		}
		
	}
	
}
