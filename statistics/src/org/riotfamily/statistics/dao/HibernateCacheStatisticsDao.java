package org.riotfamily.statistics.dao;

import org.hibernate.SessionFactory;
import org.riotfamily.statistics.domain.Statistics;

public class HibernateCacheStatisticsDao extends AbstractSimpleStatsDao {

	private SessionFactory sessionFactory;
	
	public HibernateCacheStatisticsDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected void populateStats(Statistics stats) throws Exception {
		org.hibernate.stat.Statistics hs = sessionFactory.getStatistics();
		
		stats.add("Query cache hit count", hs.getQueryCacheHitCount());
		stats.add("Query cache miss count", hs.getQueryCacheMissCount());
		stats.add("Query cache put count", hs.getQueryCachePutCount());

		stats.add("2nd level cache hit count" , hs.getSecondLevelCacheHitCount());
		stats.add("2nd level cache miss count", hs.getSecondLevelCacheMissCount());
		stats.add("2nd level cache put count", hs.getSecondLevelCachePutCount());
	}
	
}
