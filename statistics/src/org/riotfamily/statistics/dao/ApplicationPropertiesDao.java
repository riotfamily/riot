package org.riotfamily.statistics.dao;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.Resource;

public class ApplicationPropertiesDao extends AbstractPropertiesDao {

	private Resource resource;
	
	protected Map getProperties() { 
		Properties result = new Properties();
		try {
			result.load(getResource().getInputStream());
		} 
		catch (IOException e) {
			result.setProperty("Error", "Reading properties failed: " + e.toString());
		}
		return result;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
