package org.riotfamily.forms.bind;

import org.riotfamily.forms.Element;

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
	 */
	public String getFieldName();
	
}
