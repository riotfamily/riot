package org.riotfamily.common.servlet;

import org.riotfamily.common.util.RiotLog;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Extends the {@link SimpleMappingExceptionResolver} and adds the
 * root-cause of the exception to the model.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class AdvancedMappingExceptionResolver 
		extends SimpleMappingExceptionResolver {

	private String rootCaseAttribute = "rootCause";
	
	private RiotLog log = RiotLog.get(this);
	
	public void setRootCaseAttribute(String rootCaseAttribute) {
		this.rootCaseAttribute = rootCaseAttribute;
	}

	@SuppressWarnings("unchecked")
	protected ModelAndView getModelAndView(String viewName, Exception ex) {
		log.error("Unhandled exception", ex);
		ModelAndView mv = super.getModelAndView(viewName, ex);

		Throwable rootCause = ex; 
		while (rootCause.getCause() != null) {
			rootCause = rootCause.getCause();
		}
		mv.getModel().put(rootCaseAttribute, rootCause);
		
		return mv;
	}

}
