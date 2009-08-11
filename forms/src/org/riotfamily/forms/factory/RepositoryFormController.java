package org.riotfamily.forms.factory;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.AjaxFormController;
import org.riotfamily.forms.controller.FormContextFactory;



/**
 * FormController that creates forms using a {@link FormRepository}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class RepositoryFormController extends AjaxFormController {
	
	protected static final String DEFAULT_FORM_ID_PARAM = "form";
	
	private FormRepository formRepository;
	
	private String formIdParam = DEFAULT_FORM_ID_PARAM;
	
	
	public RepositoryFormController(FormContextFactory formContextFactory,
			FormRepository formRepository) {
		
		super(formContextFactory);
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

	/**
	 * Returns the name of the attribute under which the {@link Form} is
	 * stored in the HTTP session. This implementation returns the 
	 * {@link #getFormId(HttpServletRequest) formId} with the controller's
	 * class name as prefix.  
	 */
	protected String getSessionAttribute(HttpServletRequest request) {
		return RepositoryFormController.class.getName()	
				+ '.' + getFormId(request);
	}

	/**
	 * Returns the id of the form to be used. The default implementation 
	 * returns the value of the {@link #setFormIdParam(String) formIdParam}
	 * request parameter.
	 */
	protected String getFormId(HttpServletRequest request) {
		return request.getParameter(formIdParam);
	}
	
	protected Form createForm(HttpServletRequest request) {
		String formId = getFormId(request);
		log.debug("Creating form with id " + formId);
		return formRepository.createForm(formId);
	}

}
