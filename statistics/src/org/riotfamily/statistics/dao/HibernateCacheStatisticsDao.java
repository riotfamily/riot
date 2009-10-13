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
