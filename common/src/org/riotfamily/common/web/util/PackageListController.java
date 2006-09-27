package org.riotfamily.common.web.util;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.PackageLister;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PackageListController implements Controller {

	public static final String MODEL_NAME = "packages";
	
	private String[] patterns;
	
	private String viewName;
	
	public void setPatterns(String[] patterns) {
		this.patterns = patterns;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		Collection packages = PackageLister.listPackages(patterns);
		return new ModelAndView(viewName, MODEL_NAME, packages);
	}
}
