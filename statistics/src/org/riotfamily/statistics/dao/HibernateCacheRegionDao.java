package org.riotfamily.statistics.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.statistics.domain.CacheRegionStatistics;
import org.springframework.dao.DataAccessException;

public class HibernateCacheRegionDao extends AbstractNamedEntityDao {

	private SessionFactory sessionFactory;
	
	public Class getEntityClass() {
		return CacheRegionStatistics.class;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}	

	protected List listInternal(Object parent, ListParams params) {
		ArrayList result = new ArrayList();
		String[] regions = getSessionFactory().getStatistics().getSecondLevelCacheRegionNames();
		for (int i = 0; i < regions.length; i++) {
			CacheRegionStatistics entity = new CacheRegionStatistics(regions[i]);
			SecondLevelCacheStatistics stats = getSessionFactory().getStatistics().getSecondLevelCacheStatistics(regions[i]);
			entity.setElementsInMemory(new Long(stats.getElementCountInMemory()));
			entity.setElementsOnDisk(new Long(stats.getElementCountOnDisk()));
			entity.setHitCount(new Long(stats.getHitCount()));
			entity.setMissCount(new Long(stats.getMissCount()));
			entity.setPutCount(new Long(stats.getPutCount()));
			entity.setKbInMemory(new Long(stats.getSizeInMemory() / 1024));
			result.add(entity);
		}
		return result;
	}
	
	
	public Object load(String id) throws DataAccessException {
		return new CacheRegionStatistics(id);
	}
}
