package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;


/**
 * ComponentKeyResolver that always returns the value that has been specified
 * as constructor argument.
 */
public class FixedComponentKeyResolver implements ComponentKeyResolver {

	private String key;
	
	public FixedComponentKeyResolver(String key) {
		this.key = key;
	}

	public String getComponentKey(HttpServletRequest request) {
		return key;
	}

}
