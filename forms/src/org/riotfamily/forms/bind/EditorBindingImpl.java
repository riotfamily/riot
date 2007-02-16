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