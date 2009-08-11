package org.riotfamily.riot.job.support;

import org.riotfamily.common.scheduling.ScheduledTaskSupport;
import org.riotfamily.riot.job.JobManager;
import org.riotfamily.riot.job.model.JobDetail;
import org.springframework.beans.factory.annotation.Required;

public class ScheduledJob extends ScheduledTaskSupport {

	private JobManager jobManager;
	
	private String jobType;

	@Required
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	@Required
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void execute() throws Exception {
		JobDetail jobDetail = jobManager.getOrCreateJob(jobType, null, false);
		if (jobDetail.getState() == JobDetail.INITIALIZED
				|| jobDetail.getState() == JobDetail.INTERRUPTED) {
			
			jobManager.executeJob(jobDetail);
		}
	}
	
}
