package org.riotfamily.riot.job.ui;

import java.util.List;

import org.directwebremoting.WebContextFactory;
import org.riotfamily.riot.job.JobManager;
import org.riotfamily.riot.job.persistence.JobDao;
import org.riotfamily.riot.job.persistence.JobDetail;

public class JobUIService {

	private JobManager manager;
	
	private JobDao dao;
	
	private JobUIUpdater updater;
	
	public JobUIService(JobManager manager, JobDao dao, JobUIUpdater updater) {
		this.manager = manager;
		this.dao = dao;
		this.updater = updater;
	}
	
	public JobDetail getJobDetail(Long jobId) {
		updater.register(WebContextFactory.get(), jobId);
		return dao.getJobDetail(jobId);
	}
	
	public List getLogEntries(Long jobId) {
		return dao.getLogEntries(jobId);
	}
	
	public void executeJob(Long jobId) {
		manager.executeJob(dao.getJobDetail(jobId));
	}
	
	public void interruptJob(Long jobId) {
		manager.interruptJob(dao.getJobDetail(jobId));
	}
	
	public void cancelJob(Long jobId) {
		manager.cancelJob(dao.getJobDetail(jobId));
	}
	
}
