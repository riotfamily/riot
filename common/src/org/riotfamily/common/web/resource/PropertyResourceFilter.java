package org.riotfamily.common.web.resource;

import java.io.FilterReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.io.PropertyFilterReader;
import org.springframework.util.Assert;

public class PropertyResourceFilter extends AbstractPathMatchingResourceFilter {

	public static final String CONTEXT_PATH_PROPERTY = "contextPath";
	
	private Properties properties;
	
	private boolean exposeContextPath = true;
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public void setPropertiesMap(Map map) {
		properties = new Properties();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Assert.isInstanceOf(String.class, entry.getKey(), 
					"Map must only contain String keys.");
			
			Assert.isInstanceOf(String.class, entry.getValue(), 
					"Map must only contain String values.");
			
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			properties.setProperty(key, value);
		}
	}
	
	public void setExposeContextPath(boolean exposeContextPath) {
		this.exposeContextPath = exposeContextPath;
	}

	public FilterReader createFilterReader(Reader in, HttpServletRequest request) {
		Properties props = properties;
		if (exposeContextPath) {
			props = new Properties(properties);
			props.setProperty(CONTEXT_PATH_PROPERTY, request.getContextPath());
		}
		return new PropertyFilterReader(in, props);
	}

}
