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

import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;

public class C3P0StatisticsDao extends AbstractSimpleStatsDao {

	private AbstractPoolBackedDataSource ds;
	
	public void setDataSource(AbstractPoolBackedDataSource ds) {
		this.ds = ds;
	}

	@Override
	protected void populateStats(Statistics stats) throws Exception {
		stats.add("Connections", ds.getNumConnections());
		stats.add("Busy connections", ds.getNumBusyConnections());
		stats.add("Idle connections", ds.getNumIdleConnections());
		
		stats.addOkBelow("Failed check-ins ", ds.getNumFailedCheckinsDefaultUser(), 0);
		stats.addOkBelow("Failed check-outs", ds.getNumFailedCheckoutsDefaultUser(), 0);
		stats.addOkBelow("Failed idle-tests", ds.getNumFailedIdleTestsDefaultUser(), 0);
		
		stats.add("Threads waiting for checkout", ds.getNumThreadsAwaitingCheckoutDefaultUser());
		stats.addOkBelow("Unclosed orphaned connections", ds.getNumUnclosedOrphanedConnections(), 0);
		
		stats.addOkIfNull("Last acquisition failure", ds.getLastAcquisitionFailureDefaultUser());
		stats.addOkIfNull("Last check-in failure", ds.getLastCheckinFailureDefaultUser());
		stats.addOkIfNull("Last check-out failure", ds.getLastCheckoutFailureDefaultUser());
		stats.addOkIfNull("Last connection test failure", ds.getLastConnectionTestFailureDefaultUser());
		stats.addOkIfNull("Last idle test failure", ds.getLastIdleTestFailureDefaultUser());
	}
}
