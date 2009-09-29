/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.statistics.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.hibernate.SessionFactory;
import org.hibernate.cache.entry.CacheEntry;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.riotfamily.common.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.statistics.domain.CacheRegionStatsItem;

public class ClearCacheRegionCommand extends AbstractHibernateCacheCommand {

	private Logger log = LoggerFactory.getLogger(ClearCacheRegionCommand.class);
	
	public ClearCacheRegionCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	protected String getIcon(String action) {
		return "clear";
	}
	
	public void doExecute(CommandContext context, Selection selection) {
		for (SelectionItem item : selection) {
			CacheRegionStatsItem crs = (CacheRegionStatsItem) item.getObject();
			clearHibernateCacheRegion(crs.getName());	
		}
	}
	
	private void clearHibernateCacheRegion(String region) {
		try {
			SecondLevelCacheStatistics stats = getSessionFactory().getStatistics().getSecondLevelCacheStatistics(region);
			Set<String> classes = getCacheEntrySet(stats);
			for (Iterator<String> iterator = classes.iterator(); iterator.hasNext();) {
				String clazz = iterator.next();
				evictCacheEntry(clazz, false);
			}
		} 
		catch (Exception e) {
			log.warn("Clearing cache region failed: " + region);
			throw new RuntimeException("Clearing cache region failed");
		}
	}
	
	private Set<String> getCacheEntrySet(SecondLevelCacheStatistics slStats) {
		Set<String> entities = Generics.newHashSet();
		
		/* Liefert z.Zt ClassCastException.
		 * (http://opensource.atlassian.com/projects/hibernate/browse/HHH-2815)
		 */
		Map<?,?> entries = (Map<?,?>) slStats.getEntries();
		
		if (slStats != null && entries != null) {
			for (Iterator<?> iterator = entries.entrySet().iterator(); iterator.hasNext();) {
				Entry<?,?> entry = (Entry<?,?>) iterator.next();
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
