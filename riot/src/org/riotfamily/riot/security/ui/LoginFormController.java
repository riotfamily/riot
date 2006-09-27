package org.riotfamily.riot.security.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.common.web.util.ServletMappingHelper;
import org.riotfamily.riot.security.LoginManager;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

public class LoginFormController implements Controller, 
		UrlMappingAware, BeanNameAware {

	private LoginManager loginManager;
	
	private String viewName = ResourceUtils.getPath(
			LoginFormController.class, "LoginFormView.ftl");
	
	private String successViewName;
	
	private UrlMapping urlMapping;
	
	private String beanName;
	
	private ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper();
	
	public LoginFormController(LoginManager loginManager) {
		this.loginManager = loginManager;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuccessViewName(String successViewName) {
		this.successViewName = successViewName;
	}

	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
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
					String successUrl = servletMappingHelper.getRootPath(request);
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
	
	public String getLoginFormUrl(HttpServletRequest request) {
		return request.getContextPath() + urlMapping.getUrl(beanName, null);
	}

}
