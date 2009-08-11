package org.riotfamily.core.security.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.core.security.session.LoginManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

public class LogoutController implements Controller {

	private String servletPrefix = "";
	
	private String goodbyUrl = "/";
	
	public void setServletPrefix(String servletPrefix) {
		this.servletPrefix = servletPrefix;
	}

	public void setGoodbyUrl(String goodbyUrl) {
		this.goodbyUrl = goodbyUrl;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		LoginManager.logout(request, response);
		return new ModelAndView(new RedirectView(servletPrefix + goodbyUrl, true));
	}

}
