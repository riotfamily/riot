package org.riotfamily.forms.bind;

import java.beans.PropertyEditor;


/**
 * Value object that stores a binding between a property and an editor.
 * 
 * @see org.riotfamily.forms.bind.EditorBinder 
 */
public class EditorBindingImpl implements EditorBinding {

	private EditorBinder editorBinder;
	
	private Editor editor;

	private String property;

	
	public EditorBindingImpl(EditorBinder editorBinder, Editor editor, 
			String property) {
		
		this.editorBinder = editorBinder;
		this.editor = editor;
		this.property = property;
	}

	public EditorBinder getEditorBinder() {
		return editorBinder;
	}

	public Editor getEditor() {
		return editor;
	}

	public String getProperty() {
		return property;
	}
	
	public Object getValue() {
		return editorBinder.getPropertyValue(getProperty());
	}
	
	public Class getBeanClass() {
		return editorBinder.getBeanClass();
	}
	
	public String getPropertyPath() {
		if (editorBinder.getParent() != null) {
			return editorBinder.getParent().getPropertyPath() 
					+ '.' + getProperty(); 
		}
		return getProperty();
	}
	
	public Class getPropertyType() {
		return editorBinder.getPropertyType(property);
	}
	
	public PropertyEditor getPropertyEditor() {
		return editorBinder.getPropertyEditor(getPropertyType(), getPropertyPath());
	}
	
}