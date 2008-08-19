package org.riotfamily.statistics.dao;

import java.util.List;

import org.riotfamily.statistics.domain.StatsItem;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class CriticalRequestStatisticsDao extends AbstractStatsItemDao {

	private RequestCountFilterPlugin filterPlugin;
		
	public CriticalRequestStatisticsDao(RequestCountFilterPlugin filterPlugin) {
		this.filterPlugin = filterPlugin;
	}

	@Override
	protected List<? extends StatsItem> getStats() {
		return filterPlugin.getCriticalRequests();
	}
}
