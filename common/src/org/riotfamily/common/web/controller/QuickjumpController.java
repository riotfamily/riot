package org.riotfamily.common.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

public class QuickjumpController implements Controller {

	private String urlParameter = "url";
	
	public void setUrlParameter(String urlParameter) {
		this.urlParameter = urlParameter;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		String url = request.getParameter(urlParameter);
		if (StringUtils.hasLength(url) && !url.startsWith("#")) {
			return new ModelAndView(new RedirectView(url));
		}
		else {
			response.sendError(HttpServletResponse.SC_NO_CONTENT);
			return null;
		}
	}
}
