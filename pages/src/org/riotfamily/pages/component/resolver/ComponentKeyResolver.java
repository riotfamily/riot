package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;

public interface ComponentKeyResolver {

	public String getComponentKey(HttpServletRequest request);
}
