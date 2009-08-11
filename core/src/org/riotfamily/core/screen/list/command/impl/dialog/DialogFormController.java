package org.riotfamily.core.screen.list.command.impl.dialog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.AjaxFormController;
import org.riotfamily.forms.controller.FormContextFactory;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DialogFormController extends AjaxFormController {
	
	
	public DialogFormController(FormContextFactory formContextFactory) {
		super(formContextFactory);
	}
	
	/**
	 * Instead of creating a new form, this method looks up the form instance,
	 * that has been previously created by the DialogCommand.
	 */
	protected Form createForm(HttpServletRequest request) {
		Form form = getForm(request);
		if (form.getButtons().isEmpty()) {
			form.addButton("execute");
		}
		return form;
	}

	/**
	 * Returns the name of the session attribute under which the form is stored.
	 * This implementation expects that the controller is mapped via a handler
	 * mapping which exposes a request attribute called 'formKey' that contains
	 * the name of the session attribute.
	 */
	protected String getSessionAttribute(HttpServletRequest request) {
		return (String) request.getAttribute("formKey");
	}
	
	/**
	 * Overwrites the super implementation to do nothing since the form
	 * created by the command is already populated by contract.
	 */
	protected void populateForm(Form form, HttpServletRequest request) {
	}
	
	public ModelAndView handleFormSubmission(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		return null;
	}	
	
}
