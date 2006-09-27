package org.riotfamily.pages.component.property;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * PropertyProcessor that initializes properties with the specified default 
 * values.
 */
public class DefaultValuePropertyProcessor implements PropertyProcessor {

	private Properties values;
	
	public void setValues(Properties values) {
		this.values = values;
	}

	/**
	 * Iterates over the defaults and checks whether a value is already set.
	 * If no matching entry is found in the map, the default value is added
	 * to the map. 
	 */
	public void resolveStrings(Map map) {
		if (values != null) {
			Enumeration en = values.propertyNames();
			while (en.hasMoreElements()) {
				String prop = (String) en.nextElement();
				if (!map.containsKey(prop)) {
					map.put(prop, values.getProperty(prop));
				}
			}
		}
	}
	
	/**
	 * Does nothing.
	 */
	public void convertToStrings(Map map) {
	}

	/**
	 * Does nothing.
	 */
	public void copy(Map source, Map dest) {
	}

	/**
	 * Does nothing.
	 */
	public void delete(Map map) {
	}

}
