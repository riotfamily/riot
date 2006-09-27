package org.riotfamily.pages.mvc.hibernate;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.PropertyUtils;


public abstract class AbstractParameterResolver implements ParameterResolver {
	
	private String name;
	
	private String property;
		
	public void setParam(String name) {
		this.name = name;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	protected String getName() {
		return name;
	}

	public boolean accept(String name) {
		return this.name.equals(name);
	}
	
	public boolean includeInCacheKey() {
		return true;
	}
	
	public Object getValue(HttpServletRequest request) {
		Object value = getValueInternal(request);
		if (property != null && value != null) {
			value = PropertyUtils.getProperty(value, property);
		}
		return value;
	}
	
	protected abstract Object getValueInternal(HttpServletRequest request);

}
