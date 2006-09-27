package org.riotfamily.pages.mvc.hibernate;

import javax.servlet.http.HttpServletRequest;

/**
 * ParameterResolver that first looks for a HTTP parameter with name returned
 * by <code>getName()</code>. If parameter is empty it returns null.
 * If no parameter is found, it looks for a request attribute with the name
 * returned by <code>getAttribute()</code>.
 */
public class DefaultParameterResolver extends AbstractParameterResolver {

	private String attribute;
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	public String getAttribute() {
		return attribute != null ? attribute : getName();
	}

	public Object getValueInternal(HttpServletRequest request) {
		Object value = request.getParameter(getName());
		if (value == null) {
			value = request.getAttribute(getAttribute());
		} else if (((String) value).length() == 0) {
			return null;
		}
		return value;
	}
	
}
