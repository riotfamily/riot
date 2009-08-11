package org.riotfamily.statistics.dao;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ResourcePropertiesDao extends AbstractPropertiesDao {

	private Resource resource;

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected Map<String, String> getProperties() throws Exception {
		return (Map) PropertiesLoaderUtils.loadProperties(resource);
	}
}
