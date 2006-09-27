package org.riotfamily.riot.job.command;

import org.riotfamily.riot.job.JobManager;
import org.riotfamily.riot.job.persistence.JobDetail;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class JobCommand extends AbstractCommand implements 
		ApplicationContextAware {

	public static final String JOB_STATUS_ACTION = "jobStatus";
	
	private JobManager jobManager;
	
	private String jobType;
	
	private RiotRuntime runtime;
	
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	public void setApplicationContext(ApplicationContext context) {
		runtime = (RiotRuntime) BeanFactoryUtils.beanOfTypeIncludingAncestors(
				context, RiotRuntime.class);
	}
	
	public boolean isEnabled(RenderContext context) {
		return true;
	}
	
	public CommandResult execute(CommandContext context) {
		String objectId = context.getObjectId() != null 
				? context.getObjectId() : context.getParentId();
				
		JobDetail detail = jobManager.getOrCreateJob(jobType, objectId);
		return new GotoUrlResult(runtime.getServletPrefix() + "/job?jobId=" 
				+ detail.getId());
	}
}
