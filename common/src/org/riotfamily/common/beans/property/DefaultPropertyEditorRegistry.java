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
