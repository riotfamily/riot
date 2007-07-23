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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms;

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
	
}
