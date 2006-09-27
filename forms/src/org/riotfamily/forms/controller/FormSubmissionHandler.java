package org.riotfamily.forms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.Form;
import org.springframework.web.servlet.ModelAndView;

public interface FormSubmissionHandler {

	public ModelAndView handleFormSubmission(Form form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}
