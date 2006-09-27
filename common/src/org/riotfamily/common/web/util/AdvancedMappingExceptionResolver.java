package org.riotfamily.common.web.util;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class AdvancedMappingExceptionResolver 
		extends SimpleMappingExceptionResolver {

	private String rootCaseAttribute = "rootCause";
	
	public void setRootCaseAttribute(String rootCaseAttribute) {
		this.rootCaseAttribute = rootCaseAttribute;
	}

	protected ModelAndView getModelAndView(String viewName, Exception ex) {
		logger.error("Unhandled exception", ex);
		ModelAndView mv = super.getModelAndView(viewName, ex);
		
		Throwable rootCause = ex; 
		while (rootCause.getCause() != null) {
			rootCause = rootCause.getCause();
		}
		mv.getModel().put(rootCaseAttribute, rootCause);
		
		return mv;
	}

}
