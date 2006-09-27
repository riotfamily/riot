package org.riotfamily.forms.bind;

import java.beans.PropertyEditor;

/**
 * Provides information about a bound editor.
 */
public interface EditorBinding {

	/**
	 * Returns the EditorBinder. Can be used by elements to access other 
	 * properties than the one they are bound to.
	 */
	public EditorBinder getEditorBinder();

	/**
	 * Returns the Editor.
	 */
	public Editor getEditor();

	/**
	 * Returns the property name.
	 */
	public String getProperty();
	
	/**
	 * Returns the actual property value.
	 */
	public Object getValue();
	
	/**
	 * Returns the type of the bean the property belongs to.
	 */
	public Class getBeanClass();
	
	/**
	 * Returns the property path.
	 */
	public String getPropertyPath();
	
	/**
	 * Returns the type of the property. 
	 */
	public Class getPropertyType();
	
	/**
	 * Returns a PropertyEditor capable of handling the property type.
	 */
	public PropertyEditor getPropertyEditor();
	
}