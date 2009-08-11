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
		stats.add("Close statement count", hs.getCloseStatementCount());
		
		stats.add("Query execution count", hs.getQueryExecutionCount());
		stats.addOkBelow("Query execution max time", hs.getQueryExecutionMaxTime(), 1000);
		stats.add("Slowest statement", hs.getQueryExecutionMaxTimeQueryString());
	}
	
}
