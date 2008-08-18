package org.riotfamily.statistics.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.stat.Statistics;

public class HibernateCommonStatisticsDao extends AbstractHibernateStatisticsDao {

	protected Map getProperties() {
		Statistics stats = getSessionFactory().getStatistics();
		
		Map result = new LinkedHashMap();
		result.put("Hibernate statistics", "" + (stats.isStatisticsEnabled() ? "enabled" : "disabled" ));
		result.put("Start time", "" + new Date(stats.getStartTime()));
		result.put("Connect count", "" + stats.getConnectCount());
		result.put("Close statement count", "" + stats.getCloseStatementCount());
		result.put("Flush count", "" + stats.getFlushCount());
		result.put("Session open count", "" + stats.getSessionOpenCount());
		result.put("Session close count", "" + stats.getSessionCloseCount());
		result.put("Transaction count", "" + stats.getTransactionCount());
		result.put("Successful transaction count", "" + stats.getSuccessfulTransactionCount());
		result.put("Optimistic failure count", "" + stats.getOptimisticFailureCount());
		result.put("Prepare statement count", "" + stats.getPrepareStatementCount());
		result.put("Query execution count", "" + stats.getQueryExecutionCount());
		result.put("Query execution max time", "" + stats.getQueryExecutionMaxTime());
		result.put("Query execution critical statement", stats.getQueryExecutionMaxTimeQueryString());
		result.put("Query execution max time", "" + stats.getQueryExecutionMaxTime());
		
		return result;
	}

}
