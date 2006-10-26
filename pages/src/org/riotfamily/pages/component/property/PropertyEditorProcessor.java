package org.riotfamily.pages.component.property;

import java.beans.PropertyEditor;

/**
 * PropertyProcessor implementation that performs conversions using a
 * PropertyEditor.
 */
public class PropertyEditorProcessor extends AbstractSinglePropertyProcessor {

	private PropertyEditor propertyEditor;
	
	private String defaultValue;
	
	public PropertyEditorProcessor() {
	}

	public PropertyEditorProcessor(String property, 
			PropertyEditor propertyEditor) {
		
		this.propertyEditor = propertyEditor;
		setProperty(property);
	}

	public void setPropertyEditor(PropertyEditor propertyEditor) {
		this.propertyEditor = propertyEditor;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public synchronized Object resolveString(String value) {
		if (value == null) {
			value = defaultValue;
		}
		propertyEditor.setAsText(value);
		return propertyEditor.getValue();
	}

	public synchronized String convertToString(Object object) {
		if (object == null) {
			return null;
		}
		propertyEditor.setValue(object);
		return propertyEditor.getAsText();
	}

	public String copy(String value) {
		return value;
	}

	public void delete(String value) {
	}

}
