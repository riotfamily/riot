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

	@Override
	public boolean canSortBy(String property) {
		return true;
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
