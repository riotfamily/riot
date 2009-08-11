package org.riotfamily.core.resource;

import java.io.FilterReader;
import java.io.Reader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.io.PropertyFilterReader;
import org.riotfamily.common.util.Generics;
import org.springframework.web.servlet.support.RequestContextUtils;

public class PropertyResourceFilter extends AbstractPathMatchingResourceFilter {

	public static final String CONTEXT_PATH_PROPERTY = "contextPath";

	public static final String LANGUAGE_PROPERTY = "language";

	private Map<String, String> properties;
	
	private boolean exposeContextPath = true;
	
	private boolean exposeLanguage = true;
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
		
	public void setExposeContextPath(boolean exposeContextPath) {
		this.exposeContextPath = exposeContextPath;
	}
	
	public void setExposeLanguage(boolean exposeLanguage) {
		this.exposeLanguage = exposeLanguage;
	}

	public FilterReader createFilterReader(Reader in, HttpServletRequest request) {
		Map<String, String> props = Generics.newHashMap(properties);
		if (exposeContextPath) {
			props.put(CONTEXT_PATH_PROPERTY, request.getContextPath());
		}
		if (exposeLanguage) {
			props.put(LANGUAGE_PROPERTY,
					RequestContextUtils.getLocale(request).getLanguage().toLowerCase());
		}
		return new PropertyFilterReader(in, props);
	}

}
