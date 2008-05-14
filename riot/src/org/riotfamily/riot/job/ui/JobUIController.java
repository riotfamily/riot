package org.riotfamily.riot.job.ui;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.job.JobManager;
import org.riotfamily.riot.job.persistence.JobDetail;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class JobUIController implements Controller {

	private JobManager jobManager;
	
	private String viewName = ResourceUtils.getPath(
				JobUIController.class, "JobView.ftl");
	
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String objectId = request.getParameter("objectId");
		String jobType = request.getParameter("type");
		
		JobDetail detail = jobManager.getOrCreateJob(jobType, objectId);
		
		HashMap model = new HashMap();
		model.put("jobId", detail.getId());
		model.put("type", jobType);
		
		return new ModelAndView(viewName, model);
	}
	
	
}
