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
package org.riotfamily.common.beans.property;

import java.beans.PropertyEditor;

import org.springframework.beans.PropertyEditorRegistrySupport;

/**
 * PropertyEditorRegistry that provides a method to return either a custom
 * editor or a suitable default editor as fallback.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class DefaultPropertyEditorRegistry 
		extends PropertyEditorRegistrySupport {

	public DefaultPropertyEditorRegistry() {
		registerDefaultEditors();
	}
	
	public PropertyEditor findEditor(Class<?> requiredType) {
		return findEditor(requiredType, null);
	}

	public PropertyEditor findEditor(Class<?> requiredType, String propertyPath) {
		PropertyEditor pe = findCustomEditor(requiredType, propertyPath);
		if (pe == null) {
			pe = getDefaultEditor(requiredType);
		}
		return pe;
	}

}
