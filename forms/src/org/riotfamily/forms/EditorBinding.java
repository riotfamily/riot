/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	public Class<?> getBeanClass();
	
	/**
	 * Returns the property path.
	 */
	public String getPropertyPath();
	
	/**
	 * Returns the type of the property. 
	 */
	public Class<?> getPropertyType();
	
	/**
	 * Returns a PropertyEditor capable of handling the property type.
	 */
	public PropertyEditor getPropertyEditor();
	
	/**
	 * Returns whether the edited bean existed before, or whether it was 
	 * created by the current form.
	 */
	public boolean isEditingExistingBean();
	
}