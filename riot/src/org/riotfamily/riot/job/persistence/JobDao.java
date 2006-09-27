package org.riotfamily.riot.job.persistence;

import java.util.List;


public interface JobDao {

	public List getLogEntries(Long jobId);
	
	public void log(JobLogEntry entry);
	
	public List getJobDetails();
	
	public List getPendingJobDetails();
	
	public JobDetail getJobDetail(Long jobId);
	
	public JobDetail getPendingJobDetail(String type, String objectId);
	
	public int getAverageStepTime(String type);
	
	public void saveJobDetail(JobDetail detail);
	
	public void updateJobDetail(JobDetail detail);

}
