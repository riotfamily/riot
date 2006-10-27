package org.riotfamily.pages.component.property;

import java.util.Map;

/**
 * Abstract base class for PropertyProcessors that process a single property.
 */
public abstract class AbstractSinglePropertyProcessor 
		implements PropertyProcessor {

	private String property;
	
	public AbstractSinglePropertyProcessor() {
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void resolveStrings(Map map) {
		Object value = map.get(property);
		if (value instanceof String) {
			map.put(property, resolveString((String) value));
		}
	}
	
	protected abstract Object resolveString(String s);
	
	public void convertToStrings(Map map) {
		Object object = map.get(property);
		if (!(object instanceof String)) {
			map.put(property, convertToString(object));
		}
	}

	protected abstract String convertToString(Object object);
	
	public void copy(Map source, Map dest) {
		String s = (String) source.get(property);
		dest.put(property, copy(s));
	}
	
	protected String copy(String s) {
		return s;
	}

	public void delete(Map map) {
		String s = (String) map.get(property);
		delete(s);
	}
	
	protected void delete(String s) {
	}

}
