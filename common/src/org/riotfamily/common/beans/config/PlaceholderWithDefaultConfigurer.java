package org.riotfamily.common.beans.config;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Subclass of PropertyPlaceholderConfigurer that allows to define a default
 * value.
 */
public class PlaceholderWithDefaultConfigurer 
		extends PropertyPlaceholderConfigurer {

	public static final String DEFAULT_VALUE_SEPARATOR = "=";
	
	private String valueSeparator = DEFAULT_VALUE_SEPARATOR;
	
	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	protected String resolvePlaceholder(String placeholder, Properties props, 
			int systemPropertiesMode) {
		
		String defaultValue = null;
		int i = placeholder.indexOf(valueSeparator);
		if (i != -1) {
			if (i + 1 < placeholder.length()) {
				defaultValue = placeholder.substring(i + 1);
			}
			placeholder = placeholder.substring(0, i); 
		}
		String value = super.resolvePlaceholder(placeholder, props, 
				systemPropertiesMode);
		
		if (value == null) {
			value = defaultValue;
		}
		
		return value;
	}
	

}
