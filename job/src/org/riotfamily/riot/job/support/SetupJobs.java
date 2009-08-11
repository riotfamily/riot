package org.riotfamily.riot.job.support;

import java.util.List;

import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.context.RiotLogJobContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class SetupJobs implements InitializingBean {
	
	private List<Job> jobs;
	
	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
	
	@Transactional
	public void afterPropertiesSet() throws Exception {		
		Assert.notNull(jobs, "Jobs must not be null");
		JobContext context = new RiotLogJobContext();
		for (Job job : jobs) {
			job.execute(context);
		}
	}

}
