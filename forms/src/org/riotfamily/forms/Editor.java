package org.riotfamily.forms;

import org.riotfamily.forms.event.ChangeListener;

/**
 * Interface to be implemented by form elements that are capable of editing
 * a property value.
 */
public interface Editor extends Element {

	/**
	 * Sets the (initial) value, typically retrieved from a backing object.
	 */
	public void setValue(Object value);
	
	/**
	 * Returns the value.
	 */
	public Object getValue();
		
	/**
	 * Returns the label.
	 */
	public String getLabel();

	public void setEditorBinding(EditorBinding binding);
	
	/**
	 * Implementors must return the binding set via 
	 * {@link #setEditorBinding(EditorBinding) setEditorBinding()}.
	 */
	public EditorBinding getEditorBinding();
	
	/**
	 * Sets the fieldName that is used to build error codes. 
	 * If <code>null</code>, {@link EditorBinding#getPropertyPath()} is used.
	 * You should only use this method for unbound elements that are part
	 * of complex (composite) widgets.
	 */
	public void setFieldName(String fieldName);
	
	/**
	 * Returns the set (or computed) fieldName.
	 */
	public String getFieldName();
	
	public void addChangeListener(ChangeListener listener);
	
}
