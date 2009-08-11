package org.riotfamily.statistics.dao;

import java.util.Map;

public class EnvironmentPropertiesDao extends AbstractPropertiesDao {

	@Override
	protected Map<String, String> getProperties() throws Exception {
		return System.getenv();
	}

}
