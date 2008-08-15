package org.riotfamily.statistics.dao;

import java.util.Map;

public class EnvironmentPropertiesDao extends AbstractPropertiesDao {

	protected Map getProperties() {
		return System.getenv();
	}

}
