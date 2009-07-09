package org.riotfamily.statistics.commands;

public abstract class AbstractHibernateCacheCommand extends AbstractHibernateStatisticsCommand  {

	protected void clearCache(String entityPrefix, boolean inverse) {
		String[] entityNames = getSessionFactory().getStatistics().getEntityNames();
		for (int i = 0; i < entityNames.length; i++) {
			boolean match = entityNames[i].startsWith(entityPrefix);
			if ((match && !inverse) || (!match && inverse)){
				evictCacheEntry(entityNames[i], false);
			}
		}
	}
	
	protected void evictCacheEntry(String entity, boolean collection) {
		try {
			if (collection) {
				getSessionFactory().evictCollection(entity);
			} else {
				Class clazz = Class.forName(entity);
				getSessionFactory().evict(clazz);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid entity: " + entity + ".");
		}

	}
}
