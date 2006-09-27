package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContextUtils;

public class LocaleComponentPathResolver implements ComponentPathResolver {
	
	public String getComponentPath(HttpServletRequest request) {
		return RequestContextUtils.getLocale(request).getLanguage();
	}
	
	public String getParentPath(String path) {
		return null;
	}

}
