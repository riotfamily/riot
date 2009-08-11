package org.riotfamily.statistics.dao;

import java.util.Map;

import org.riotfamily.statistics.domain.Statistics;

public abstract class AbstractPropertiesDao extends AbstractSimpleStatsDao {

	@Override
	protected void populateStats(Statistics stats) throws Exception {
		for (Map.Entry<String, String> entry : getProperties().entrySet()) {
			stats.add(entry.getKey(), entry.getValue());
		}
	}
	
	protected abstract Map<String, String> getProperties() throws Exception;
}
