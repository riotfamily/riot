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

	public void setEditor(Editor editor);
	
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