package org.riotfamily.statistics.commands;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.log.RiotLog;
import org.hibernate.SessionFactory;
import org.hibernate.cache.entry.CacheEntry;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.statistics.domain.CacheRegionStatsItem;

public class ClearCacheRegionCommand extends AbstractHibernateCacheCommand {

	private RiotLog log = RiotLog.get(ClearCacheRegionCommand.class);
	
	public ClearCacheRegionCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void doExecute(CommandContext context) {
		CacheRegionStatsItem crs = (CacheRegionStatsItem) context.getBean();
		clearHibernateCacheRegion(crs.getName());
	}
	
	private void clearHibernateCacheRegion(String region) {
		try {
			SecondLevelCacheStatistics stats = getSessionFactory().getStatistics().getSecondLevelCacheStatistics(region);
			Set classes = getCacheEntrySet(stats);
			for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
				String clazz = (String) iterator.next();
				evictCacheEntry(clazz, false);
			}
		} 
		catch (Exception e) {
			log.warn("Clearing cache region failed: " + region);
			throw new RuntimeException("Clearing cache region failed");
		}
	}
	
	private Set getCacheEntrySet(SecondLevelCacheStatistics slStats) {
		Set entities = new HashSet();
		
		/* Liefert z.Zt ClassCastException.
		 * (http://opensource.atlassian.com/projects/hibernate/browse/HHH-2815)
		 */
		Map entries = slStats.getEntries();
		
		if (slStats != null && entries != null) {
			for (Iterator iterator = entries.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry entry = (Map.Entry)iterator.next();
				if (entry.getValue() instanceof CacheEntry) {
					String clzz =  ((CacheEntry)entry.getValue()).getSubclass();
					if (clzz != null) {
						entities.add(clzz);
					}
				}
			}
		}
		return entities;
	}

}
