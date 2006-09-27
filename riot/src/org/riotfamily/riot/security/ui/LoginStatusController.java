package org.riotfamily.riot.security.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.security.session.SessionData;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LoginStatusController implements Controller {

	private String viewName = ResourceUtils.getPath(
			LoginStatusController.class, "LoginStatusView.ftl");
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (request.getParameter("update") != null) {
			//Currently this is just used for session keepalive.
			//So nothing to do here ...
			return null;
		}
		else {
			SessionData data = SessionData.get(request);
			return new ModelAndView(viewName, "sessionData", data);
		}
	}

}
