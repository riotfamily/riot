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
package org.riotfamily.forms;

import java.beans.PropertyEditor;
import java.util.Map;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public interface EditorBinder extends PropertyEditorRegistry {

	public Map<String, EditorBinding> getBindings();

	public EditorBinder replace(EditorBinder previousBinder);
	
	public Class<?> getBeanClass();
	
	public Object getBackingObject();

	public void setBackingObject(Object backingObject);

	public boolean isEditingExistingBean();

	public Object getPropertyValue(String property);

	public void setPropertyValue(String property, Object value);

	public Class<?> getPropertyType(String property);
	
	public PropertyEditor getPropertyEditor(Class<?> type, String propertyPath);

	/**
	 * Binds the given editor to the property with the specified name.
	 *
	 * @param editor the editor to bind
	 * @param property the name of the property the editor is to be bound to
	 */
	public void bind(Editor editor, String property);

	/**
	 * Returns the editor that is bound to the given property.
	 */
	public Editor getEditor(String property);

	/**
	 * Returns the names of all properties an editor is bound to.
	 * @since 6.4
	 */
	public String[] getBoundProperties();

	/**
	 * Initializes each editor with the property value it is bound to or
	 * <code>null<code> if the backingObject is not set.
	 *
	 * @see Editor#setValue(Object)
	 */
	public void initEditors();

	/**
	 * Sets the properties of the backingObject to the values provided by the
	 * corresponding editor. If the backingObject is <code>null</code> a new
	 * instance is created.
	 *
	 * @return the populated backingObject
	 */
	public Object populateBackingObject();
	
	public void registerPropertyEditors(PropertyEditorRegistrar[] registrars);

}