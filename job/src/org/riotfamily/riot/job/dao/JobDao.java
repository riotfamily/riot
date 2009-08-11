package org.riotfamily.riot.job.dao;

import java.util.Collection;

import org.riotfamily.riot.job.model.JobDetail;
import org.riotfamily.riot.job.model.JobLogEntry;


public interface JobDao {

	public Collection<JobLogEntry> getLogEntries(Long jobId);
	
	public void log(JobLogEntry entry);
	
	public Collection<JobDetail> getJobDetails();
	
	public Collection<JobDetail> getPendingJobDetails();
	
	public JobDetail getJobDetail(Long jobId);
	
	public JobDetail getPendingJobDetail(String type, String objectId);
	
	public JobDetail getLastCompletedJobDetail(String type, String objectId);
	
	public int getAverageStepTime(String type);
	
	public void saveJobDetail(JobDetail detail);
	
	public void updateJobDetail(JobDetail detail);

	public void deleteObsoleteJobDetails();

}
