package org.riotfamily.statistics.dao;

import java.util.List;

import org.riotfamily.statistics.domain.Statistics;
import org.riotfamily.statistics.domain.StatsItem;

public abstract class AbstractSimpleStatsDao extends AbstractStatsItemDao {

	@Override
	protected List<? extends StatsItem> getStats() throws Exception {
		Statistics stats = new Statistics();
		populateStats(stats);
		return stats.getItems();
	}
	
	protected abstract void populateStats(Statistics stats) throws Exception;
}
