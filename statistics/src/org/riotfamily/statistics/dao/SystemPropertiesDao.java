package org.riotfamily.statistics.dao;

import java.util.Map;

public class SystemPropertiesDao extends AbstractPropertiesDao {

	@Override
	@SuppressWarnings("unchecked")
	protected Map<String, String> getProperties() throws Exception {
		return (Map) System.getProperties();
	}

}
