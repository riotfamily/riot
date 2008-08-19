package org.riotfamily.statistics.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.riotfamily.common.util.Generics;
import org.riotfamily.statistics.domain.CacheRegionStatsItem;
import org.riotfamily.statistics.domain.StatsItem;
import org.springframework.dao.DataAccessException;

public class HibernateCacheRegionDao extends AbstractStatsItemDao {

	private SessionFactory sessionFactory;
	
	
	public HibernateCacheRegionDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Class<?> getEntityClass() {
		return CacheRegionStatsItem.class;
	}
	
	@Override
	protected List<? extends StatsItem> getStats() {
		ArrayList<CacheRegionStatsItem> stats = Generics.newArrayList();
		String[] regions = sessionFactory.getStatistics().getSecondLevelCacheRegionNames();
		for (String region : regions) {
			CacheRegionStatsItem item = new CacheRegionStatsItem(region);
			SecondLevelCacheStatistics sl = sessionFactory.getStatistics().getSecondLevelCacheStatistics(region);
			item.setElementsInMemory(sl.getElementCountInMemory());
			item.setElementsOnDisk(sl.getElementCountOnDisk());
			item.setHitCount(sl.getHitCount());
			item.setMissCount(sl.getMissCount());
			item.setPutCount(sl.getPutCount());
			item.setKbInMemory(sl.getSizeInMemory() / 1024);
			stats.add(item);
		}
		return stats;
	}
	
	public Object load(String id) throws DataAccessException {
		return new CacheRegionStatsItem(id);
	}
}
