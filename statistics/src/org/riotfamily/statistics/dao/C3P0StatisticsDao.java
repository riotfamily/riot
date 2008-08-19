/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
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
		
		stats.add("Failed check-ins ", ds.getNumFailedCheckinsDefaultUser());
		stats.add("Failed check-outs", ds.getNumFailedCheckoutsDefaultUser());
		stats.add("Failed idle-tests", ds.getNumFailedIdleTestsDefaultUser());
		
		stats.add("Threads waiting for checkout", ds.getNumThreadsAwaitingCheckoutDefaultUser());
		stats.add("Unclosed orphaned connections", ds.getNumUnclosedOrphanedConnections());
		
		stats.add("Last acquisition failure", ds.getLastAcquisitionFailureDefaultUser());
		stats.add("Last check-in failure", ds.getLastCheckinFailureDefaultUser());
		stats.add("Last check-out failure", ds.getLastCheckoutFailureDefaultUser());
		stats.add("Last connection test failure", ds.getLastConnectionTestFailureDefaultUser());
		stats.add("Last idle test failure", ds.getLastIdleTestFailureDefaultUser());
	}
}
