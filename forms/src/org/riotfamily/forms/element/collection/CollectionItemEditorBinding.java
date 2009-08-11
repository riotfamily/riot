package org.riotfamily.forms.element.collection;

import java.beans.PropertyEditor;

import org.riotfamily.forms.Editor;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.EditorBinding;



public class CollectionItemEditorBinding implements EditorBinding {

		private EditorBinding parentBinding;
		
		private Editor editor;
		
		private Object value;
		
		private boolean existingItem;
		
		
		public CollectionItemEditorBinding(EditorBinding parentBinding) {
			this.parentBinding = parentBinding;
		}

		public Class<?> getBeanClass() {
			return value != null ? value.getClass() : Object.class;
		}

		public boolean isEditingExistingBean() {
			return existingItem;
		}
		
		public void setExistingItem(boolean existingItem) {
			this.existingItem = existingItem;
		}
		
		public void setEditor(Editor editor) {
			this.editor = editor;
		}
		
		public Editor getEditor() {
			return editor;
		}

		public EditorBinder getEditorBinder() {
			return parentBinding.getEditorBinder();
		}

		public String getProperty() {
			return null;
		}

		public PropertyEditor getPropertyEditor() {
			return getEditorBinder().getPropertyEditor(getBeanClass(), null);
		}

		public String getPropertyPath() {
			return parentBinding.getPropertyPath() + '.' + editor.getId();
		}

		public Class<?> getPropertyType() {
			return Object.class;
		}

		public Object getValue() {
			return value;
		}
		
		public void setValue(Object value) {
			this.value = value;
		}
		
		
	}