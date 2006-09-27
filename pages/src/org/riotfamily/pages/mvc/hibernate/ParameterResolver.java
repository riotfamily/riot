package org.riotfamily.pages.mvc.hibernate;

import javax.servlet.http.HttpServletRequest;

public interface ParameterResolver {

	public boolean accept(String name);
	
	public Object getValue(HttpServletRequest request);

	public boolean includeInCacheKey();

}
