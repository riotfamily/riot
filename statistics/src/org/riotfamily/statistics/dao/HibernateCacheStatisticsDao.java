package org.riotfamily.statistics.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.stat.Statistics;

public class HibernateCacheStatisticsDao extends AbstractHibernateStatisticsDao {

	protected Map getProperties() {

		Statistics stats = getSessionFactory().getStatistics();
		Map result = new LinkedHashMap();
		result.put("Query cache hit count", "" + stats.getQueryCacheHitCount());
		result.put("Query cache miss count", "" + stats.getQueryCacheMissCount());
		result.put("Query cache put count", "" + stats.getQueryCachePutCount());

		result.put("2nd level cache hit count" , "" + stats.getSecondLevelCacheHitCount());
		result.put("2nd level cache miss count" , "" + stats.getSecondLevelCacheMissCount());
		result.put("2nd level cache put count" , "" + stats.getSecondLevelCachePutCount());
		
		return result;
	}

}
