package org.riotfamily.riot.job.support;

import org.riotfamily.common.scheduling.TransactionalScheduledTask;
import org.riotfamily.riot.job.dao.JobDao;
import org.springframework.transaction.PlatformTransactionManager;

public class JobCleanUpTask extends TransactionalScheduledTask {

	private JobDao dao;
	
	public JobCleanUpTask(PlatformTransactionManager tx, JobDao dao) {
		super(tx);
		this.dao = dao;
	}

	@Override
	protected void executeInTransaction() throws Exception {
		dao.deleteObsoleteJobDetails();
	}
	
}
