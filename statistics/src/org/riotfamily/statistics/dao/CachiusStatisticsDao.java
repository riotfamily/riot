package org.riotfamily.statistics.dao;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CachiusStatistics;
import org.riotfamily.statistics.domain.Statistics;

public class CachiusStatisticsDao extends AbstractSimpleStatsDao {

	private CachiusStatistics cachius;

	public void setCacheService(CacheService service) {
		this.cachius = service.getStatistics();
	}
	
	public CachiusStatistics getCachiusStatistics() {
		return cachius;
	}
	
	@Override
	protected void populateStats(Statistics stats) throws Exception {
		stats.add("Capacity", cachius.getCapacity());
		stats.add("Cached items", cachius.getSize());
		stats.addMillis("Average overflow interval", cachius.getAverageOverflowInterval());
		stats.add("Number of tags", cachius.getNumberOfTags());
		stats.add("Max invalidation time [ms]", cachius.getMaxInvalidationTime());
		
		stats.add("Hits", cachius.getHits());
		stats.add("Misses", cachius.getMisses());
		
		stats.add("Max update time [ms]", cachius.getMaxUpdateTime());
		stats.add("Slowest update", cachius.getSlowestUpdate());
	}
}
