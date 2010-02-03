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

import org.riotfamily.statistics.domain.Statistics;
import org.riotfamily.statistics.web.RequestStats;

public class RequestStatisticsDao extends AbstractSimpleStatsDao {

	private RequestStats requestStats;
	
	public RequestStatisticsDao(RequestStats requestStats) {
		this.requestStats = requestStats;
	}

	@Override
	protected void populateStats(Statistics stats) throws Exception {
		stats.add("Total request count", requestStats.getTotalRequestCount());
		stats.add("Total faulty response count", requestStats.getFaultyResponseCount());
		stats.add("Parallel request count (now)", requestStats.getCurrentRequestCount());
		stats.add("Parallel request count (high water mark)", requestStats.getParallelRequestsHWM());
		if (requestStats.getAvgResponseTime() >= 0) {
			stats.add("Average response time [ms]", requestStats.getAvgResponseTime());
		}
		stats.add("Total response time [min] ", (requestStats.getTotalResponseTime() / 1000 / 60));
		stats.add("Parallel request count (critical threshold)", requestStats.getMaxRequests());
		stats.add("Critical request count", requestStats.getCriticalRequestCount());	
	}
	
}
