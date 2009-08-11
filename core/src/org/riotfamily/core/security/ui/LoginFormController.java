package org.riotfamily.core.security.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.core.security.session.LoginInterceptor;
import org.riotfamily.core.security.session.LoginManager;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

public class LoginFormController implements Controller {

	private LoginManager loginManager;
	
	private String viewName = ResourceUtils.getPath(
			LoginFormController.class, "LoginForm.ftl");
	
	private String successViewName;
	

	public LoginFormController(LoginManager loginManager) {
		this.loginManager = loginManager;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuccessViewName(String successViewName) {
		this.successViewName = successViewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		String username = request.getParameter("riot-username");
		String password = request.getParameter("riot-password");
		
		if (StringUtils.hasText(username)) {
			if (loginManager.login(request, username, password)) {
				if (successViewName != null) {
					return new ModelAndView(successViewName);
				}
				else {
					String successUrl = LoginInterceptor.getInterceptedUrl(request);
					return new ModelAndView(new RedirectView(successUrl));
				}
			}
		}
		
		if (username != null) {
			return new ModelAndView(viewName, "username", username);
		}
		else {
			return new ModelAndView(viewName);
		}
	}

}
