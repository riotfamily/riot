package org.riotfamily.forms.controller;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.ajax.AjaxFormController;


/**
 *
 */
public abstract class RepositoryFormController extends AjaxFormController {
	
	protected static final String DEFAULT_FORM_ID_PARAM = "form";
	
	private FormRepository formRepository;
	
	private String formIdParam = DEFAULT_FORM_ID_PARAM;
	
	public RepositoryFormController(FormRepository formRepository) {
		this.formRepository = formRepository;
	}
	
	protected FormRepository getFormRepository() {
		return this.formRepository;
	}
	
	protected String getFormIdParam() {
		return this.formIdParam;
	}

	public void setFormIdParam(String formIdParam) {
		this.formIdParam = formIdParam;
	}

	protected String getSessionAttribute(HttpServletRequest request) {
		return RepositoryFormController.class.getName()	
				+ '.' + getFormId(request);
	}

	protected String getFormId(HttpServletRequest request) {
		return request.getParameter(formIdParam);
	}
	
	protected Form createForm(HttpServletRequest request) {
		String formId = getFormId(request);
		log.debug("Creating form with id " + formId);
		return formRepository.createForm(formId);
	}

}
