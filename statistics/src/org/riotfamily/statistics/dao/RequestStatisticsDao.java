package org.riotfamily.statistics.dao;

import org.riotfamily.statistics.domain.Statistics;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class RequestStatisticsDao extends AbstractSimpleStatsDao {

	private RequestCountFilterPlugin requestCountFilterPlugin;
	
	public RequestStatisticsDao(RequestCountFilterPlugin requestCountFilterPlugin) {
		this.requestCountFilterPlugin = requestCountFilterPlugin;
	}

	@Override
	protected void populateStats(Statistics stats) throws Exception {
		stats.add("Total request count", requestCountFilterPlugin.getTotalRequestCount());
		stats.add("Total faulty response count", requestCountFilterPlugin.getFaultyResponseCount());
		stats.add("Parallel request count (now)", requestCountFilterPlugin.getCurrentRequestCount());
		stats.add("Parallel request count (high water mark)", requestCountFilterPlugin.getParallelRequestsHWM());
		if (requestCountFilterPlugin.getAvgResponseTime() >= 0) {
			stats.add("Average response time [ms]", requestCountFilterPlugin.getAvgResponseTime());
		}
		stats.add("Total response time [min] ", (requestCountFilterPlugin.getTotalResponseTime() / 1000 / 60));
		stats.add("Parallel request count (critical threshold)", requestCountFilterPlugin.getMaxRequests());
		stats.add("Critical request count", requestCountFilterPlugin.getCriticalRequestCount());	
	}
	
}
