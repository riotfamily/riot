package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;

public interface ComponentPathResolver {

	public String getComponentPath(HttpServletRequest request);
	
	public String getParentPath(String path);

}
