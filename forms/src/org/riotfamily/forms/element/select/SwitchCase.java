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
		propertyPath = binding.getPropertyPath() + '.' + value;
	}
	
	public void initEditors() {
		editorBinder.initEditors();
	}
	
	public void populateBackingObject() {
		editorBinder.populateBackingObject();
	}
	
	public void clear() {
		editorBinder.clear();
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
	
		private EditorBinder getDelegate() {
			if (delegate == null) {
				ElementSwitch parent = (ElementSwitch) getParent();
				delegate = parent.getEditorBinding().getEditorBinder();
			}
			return delegate;
		}
		
		@Override
		public Class<?> getPropertyType(String path) {
			return getDelegate().getPropertyType(path);
		}

		public Object getBackingObject() {
			return getDelegate().getBackingObject();
		}

		public Class<?> getBeanClass() {
			return getDelegate().getBeanClass();
		}

		public Object getPropertyValue(String property) {
			return getDelegate().getPropertyValue(property);
		}

		public boolean isEditingExistingBean() {
			return getDelegate().isEditingExistingBean();
		}

		public void setPropertyValue(String property, Object value) {
			getDelegate().setPropertyValue(property, value);
		}
		
		@Override
		protected String getPropertyPath(Editor editor, String property) {
			return propertyPath + '.' + property;
		}
		
		private void clear() {
			for (String property : getBoundProperties()) {
				setPropertyValue(property, null);
			}
		}
		
	}
	
}
