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

import java.util.Date;

import org.hibernate.SessionFactory;
import org.riotfamily.statistics.domain.Statistics;

public class HibernateStatisticsDao extends AbstractSimpleStatsDao {

	private SessionFactory sessionFactory;
	
	public HibernateStatisticsDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected void populateStats(Statistics stats) throws Exception {
		org.hibernate.stat.Statistics hs = sessionFactory.getStatistics();
		stats.add("Start time", new Date(hs.getStartTime()));
		stats.add("Flush count", hs.getFlushCount());
		stats.add("Session open count", hs.getSessionOpenCount());
		stats.add("Session close count", hs.getSessionCloseCount(), 
					hs.getSessionCloseCount() < hs.getSessionOpenCount());
		
		stats.add("Transaction count", hs.getTransactionCount());
		stats.add("Successful transaction count", hs.getSuccessfulTransactionCount());
		stats.add("Optimistic failure count", hs.getOptimisticFailureCount());
		stats.add("Connect count", hs.getConnectCount());
		stats.add("Prepare statement count", hs.getPrepareStatementCount());
		stats.add("Close statement count", hs.getCloseStatementCount(),
				hs.getCloseStatementCount() < hs.getPrepareStatementCount());
		
		stats.add("Query execution count", hs.getQueryExecutionCount());
		stats.addOkBelow("Query execution max time", hs.getQueryExecutionMaxTime(), 1000);
		stats.add("Slowest statement", hs.getQueryExecutionMaxTimeQueryString());
	}
	
}
