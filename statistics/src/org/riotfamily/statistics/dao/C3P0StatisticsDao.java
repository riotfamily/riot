package org.riotfamily.statistics.dao;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.riotfamily.common.util.Generics;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.statistics.domain.SimpleStatistics;

import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;

public class C3P0StatisticsDao extends AbstractKeyValueStatisticsDao {

	private DataSource dataSource;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	protected List listInternal(Object parent, ListParams params) {
		List<SimpleStatistics> result = Generics.newArrayList();
		if (dataSource != null) {
			if (dataSource instanceof AbstractPoolBackedDataSource) {
				AbstractPoolBackedDataSource cpds = (AbstractPoolBackedDataSource) dataSource;
				try {
					result.add(new SimpleStatistics("C3P0 number of busy connections", cpds.getNumBusyConnections() + ""));
					result.add(new SimpleStatistics("C3P0 number of connections", cpds.getNumConnections() + ""));
					result.add(new SimpleStatistics("C3P0 number of failed check-ins (default user)", cpds.getNumFailedCheckinsDefaultUser()+ ""));
					result.add(new SimpleStatistics("C3P0 number of failed check-outs (default user)", cpds.getNumFailedCheckoutsDefaultUser()+ ""));
					result.add(new SimpleStatistics("C3P0 number of failed idle-tests (default user)", cpds.getNumFailedIdleTestsDefaultUser()+ ""));
					result.add(new SimpleStatistics("C3P0 number of idle connections", cpds.getNumIdleConnections()+ ""));
					result.add(new SimpleStatistics("C3P0 number of threads waiting for checkout (default user)", cpds.getNumThreadsAwaitingCheckoutDefaultUser()+ ""));
					result.add(new SimpleStatistics("C3P0 number of unclosed orphaned connections", cpds.getNumUnclosedOrphanedConnections()+ ""));
					result.add(new SimpleStatistics("C3P0 number of user pools", cpds.getNumUserPools()+ ""));
					result.add(new SimpleStatistics("C3P0 last acquisition failure (default user)", getStr(cpds.getLastAcquisitionFailureDefaultUser())));
					result.add(new SimpleStatistics("C3P0 last check-in failure (default user)", getStr(cpds.getLastCheckinFailureDefaultUser())));
					result.add(new SimpleStatistics("C3P0 last check-out failure (default user)", getStr(cpds.getLastCheckoutFailureDefaultUser())));
					result.add(new SimpleStatistics("C3P0 last connection test failure (default user)", getStr(cpds.getLastConnectionTestFailureDefaultUser())));
					result.add(new SimpleStatistics("C3P0 last idle test failure (default user)", getStr(cpds.getLastIdleTestFailureDefaultUser())));
				} 
				catch (SQLException e) {
					result.add(new SimpleStatistics("C3P0 DS Error", e.toString()));
				}
			}
		}
		return result;
	}

	private String getStr(Throwable ex) {
		return ex == null ? "-" : ex.toString() + " (Msg.: " + ex.getMessage() + ")";
	}

}
