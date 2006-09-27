package org.riotfamily.common.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

public class BooleanEditor extends PropertyEditorSupport {

	public static final String VALUE_TRUE = "true";

	public static final String VALUE_FALSE = "false";

	private Boolean defaultValue = Boolean.FALSE;
	
	public void setDefaultValue(Boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text)) {
			setValue(defaultValue);
		}
		else if (VALUE_TRUE.equalsIgnoreCase(text)) {
			setValue(Boolean.TRUE);
		}
		else if (VALUE_FALSE.equalsIgnoreCase(text)) {
			setValue(Boolean.FALSE);
		}
		else {
			throw new IllegalArgumentException(
					"Invalid boolean value [" + text + "]");
		}
	}

	public String getAsText() {
		if (Boolean.TRUE.equals(getValue())) {
			return VALUE_TRUE;
		}
		else if (Boolean.FALSE.equals(getValue())) {
			return VALUE_FALSE;
		}
		else {
			return "";
		}
	}

}
