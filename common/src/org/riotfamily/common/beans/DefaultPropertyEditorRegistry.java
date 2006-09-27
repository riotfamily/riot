package org.riotfamily.common.beans;

import java.beans.PropertyEditor;

import org.springframework.beans.PropertyEditorRegistrySupport;

public class DefaultPropertyEditorRegistry 
		extends PropertyEditorRegistrySupport {

	public DefaultPropertyEditorRegistry() {
		registerDefaultEditors();
	}
	
	public PropertyEditor findEditor(Class requiredType) {
		return findEditor(requiredType, null);
	}

	public PropertyEditor findEditor(Class requiredType, String propertyPath) {
		PropertyEditor pe = findCustomEditor(requiredType, propertyPath);
		if (pe == null) {
			pe = getDefaultEditor(requiredType);
		}
		return pe;
	}

}
