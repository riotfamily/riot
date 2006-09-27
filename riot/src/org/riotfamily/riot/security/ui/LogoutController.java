package org.riotfamily.riot.security.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.security.LoginManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LogoutController implements Controller {

	private LoginManager loginManager;
	
	private String viewName = ResourceUtils.getPath(
			LogoutController.class, "LogoutView.ftl");
	
	public LogoutController(LoginManager loginManager) {
		this.loginManager = loginManager;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		loginManager.logout(request, response);
		return new ModelAndView(viewName);
	}

}
