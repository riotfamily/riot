package org.riotfamily.riot.job.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.job.JobManager;
import org.riotfamily.riot.job.model.JobDetail;
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
		
		String jobType = ServletUtils.getRequiredStringAttribute(request, "type");
		String objectId = (String) request.getAttribute("objectId");
		
		JobDetail detail = jobManager.getOrCreateJob(jobType, objectId, true);
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject("jobId", detail.getId());
		mv.addObject("title", request.getParameter("title"));
		mv.addObject("type", jobType);
		return mv;
	}
	
	
}
