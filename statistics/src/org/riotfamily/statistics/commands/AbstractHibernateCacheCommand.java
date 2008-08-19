package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.common.util.SpringUtils;

public abstract class AbstractHibernateCacheCommand 
		extends AbstractHibernateStatisticsCommand {

	public AbstractHibernateCacheCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	protected void clearCache(String entityPrefix, boolean inverse) {
		String[] entityNames = getSessionFactory().getStatistics().getEntityNames();
		for (String name : entityNames) {
			boolean match = name.startsWith(entityPrefix);
			if (match ^ inverse) {
				evictCacheEntry(name, false);
			}
		}
	}
	
	protected void evictCacheEntry(String entity, boolean collection) {
		if (collection) {
			getSessionFactory().evictCollection(entity);
		} 
		else {
			Class<?> clazz = SpringUtils.classForName(entity);
			getSessionFactory().evict(clazz);
		}
	}
}
