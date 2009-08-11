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
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class RequestStatisticsDao extends AbstractSimpleStatsDao {

	private RequestCountFilterPlugin requestCountFilterPlugin;
	
	public RequestStatisticsDao(RequestCountFilterPlugin requestCountFilterPlugin) {
		this.requestCountFilterPlugin = requestCountFilterPlugin;
	}

	@Override
	protected void populateStats(Statistics stats) throws Exception {
		stats.add("Total request count", requestCountFilterPlugin.getTotalRequestCount());
		stats.add("Total faulty response count", requestCountFilterPlugin.getFaultyResponseCount());
		stats.add("Parallel request count (now)", requestCountFilterPlugin.getCurrentRequestCount());
		stats.add("Parallel request count (high water mark)", requestCountFilterPlugin.getParallelRequestsHWM());
		if (requestCountFilterPlugin.getAvgResponseTime() >= 0) {
			stats.add("Average response time [ms]", requestCountFilterPlugin.getAvgResponseTime());
		}
		stats.add("Total response time [min] ", (requestCountFilterPlugin.getTotalResponseTime() / 1000 / 60));
		stats.add("Parallel request count (critical threshold)", requestCountFilterPlugin.getMaxRequests());
		stats.add("Critical request count", requestCountFilterPlugin.getCriticalRequestCount());	
	}
	
}
