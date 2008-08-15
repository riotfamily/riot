package org.riotfamily.statistics.dao;

import java.util.Map;

public class SystemPropertiesDao extends AbstractPropertiesDao {

	protected Map getProperties() {
		return System.getProperties();
	}

}
