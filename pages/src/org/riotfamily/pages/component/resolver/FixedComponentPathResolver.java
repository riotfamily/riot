package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;

/**
 * ComponentPathResolver that always returns the value that has been specified
 * as constructor argument.
 */
public class FixedComponentPathResolver implements ComponentPathResolver {

	private String path;
	
	public FixedComponentPathResolver(String path) {
		this.path = path;
	}

	public String getComponentPath(HttpServletRequest request) {
		return path;
	}
	
	public String getParentPath(String path) {
		return null;
	}

}
