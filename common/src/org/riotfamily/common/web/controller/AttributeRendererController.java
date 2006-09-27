package org.riotfamily.common.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class AttributeRendererController implements Controller {

	Log log = LogFactory.getLog(AttributeRendererController.class);
	
	private String attributeName;
	
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Assert.notNull(attributeName, "Attribute Name must be specified");
		response.getWriter().print(request.getAttribute(attributeName));		
		return null;
	}
}
