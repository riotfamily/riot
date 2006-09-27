package org.riotfamily.common.io;

import java.io.Reader;
import java.util.Properties;

/**
 * FilterReader that replaces <code>${...}</code> tokens with values form a 
 * <code>java.util.Properties</code> map.
 */
public class PropertyFilterReader extends AbstractTokenFilterReader {

	private Properties properties;
	
	public PropertyFilterReader(Reader in) {
		super(in);
	}
	
	public PropertyFilterReader(Reader in, Properties properties) {
		super(in);
		this.properties = properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getReplacement(String key) {
		return properties.getProperty(key);
	}
}
