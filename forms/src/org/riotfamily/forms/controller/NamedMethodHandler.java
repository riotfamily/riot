package org.riotfamily.forms.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.Form;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class NamedMethodHandler implements FormSubmissionHandler {
	
	private static final Class[] HANDLER_PARAM_TYPES = new Class[] {
		Form.class, HttpServletRequest.class, HttpServletResponse.class
	};
	
	private Controller controller;
	
	private Method handlerMethod;
	
	public NamedMethodHandler(Controller controller, String methodName) {
		this.controller = controller;
		try {
			handlerMethod = controller.getClass().getMethod(
					methodName, HANDLER_PARAM_TYPES);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Assert.isTrue(ModelAndView.class.isAssignableFrom(
				handlerMethod.getReturnType()), 
				"Handler method must return a ModelAndView");
	}

	public ModelAndView handleFormSubmission(Form form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Object[] args = new Object[] {form, request, response};
		return (ModelAndView) handlerMethod.invoke(controller, args);
	}

}
