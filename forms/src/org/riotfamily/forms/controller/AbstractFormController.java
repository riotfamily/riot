package org.riotfamily.forms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.ContentElement;
import org.riotfamily.forms.element.support.Container;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 *
 */
public abstract class AbstractFormController implements Controller {

	public static final String FORM_SUBMISSION_HANDLER = "formSubmissionHandler";
	
	protected static final String BUTTON_CONTAINER_ID = "buttons";
	
	private static final String CONTENT_PARAM = "_content";
	
	private static final String EXCLUSIVE_PARAM = "_exclusive";
	
	protected Log log = LogFactory.getLog(getClass());
	
	private FormContextFactory formContextFactory;
	
	private boolean processNewForms;
	
	private List buttonFactories;
	

	public void setFormContextFactory(FormContextFactory contextFactory) {
		this.formContextFactory = contextFactory;
	}

	public final void setProcessNewForms(boolean processNewForms) {
		this.processNewForms = processNewForms;
	}
	
	public void setButtonFactories(List buttonFactories) {
		this.buttonFactories = buttonFactories;
	}
	
	protected void addButton(ButtonFactory buttonFactory) {
		if (buttonFactories == null) {
			buttonFactories = new ArrayList();
		}
		buttonFactories.add(buttonFactory);
	}

	protected final void initController() {
	}
	
	/**
	 * Handles a HTTP request. The workflow is:
	 * <ol>
	 *   <li>
	 *     Check if a new Form needs to be created by calling 
	 *     {@link #isInitialRequest}.
	 *   </li>
	 *   <li>
	 *     Call to {@link #createAndInitForm} in case of an initial request
	 *     or {@link #getForm} otherwise.
	 *   </li>
	 *   <li>
	 *     Check if the request is request for additional content (an
	 *     image or iframe for example) and call {@link #handleContentRequest}
	 *     or {@link #handleFormRequest}.
	 *   </li>
	 * </ol>
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Form form = null;
		if (!isInitialRequest(request)) {
			form = getForm(request);
		}
		if (form == null) {
			form = createAndInitForm(request, response);
		}

		if (isContentRequest(request)) {
			return handleContentRequest(form, request, response);
		}
		else if (isExclusiveRequest(request)){
			return handleExclusiveRequest(form, request, response);
		}
		else {
			return handleFormRequest(form, request, response);
		}
	}
	
	protected ModelAndView handleFormRequest(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		if (!isInitialRequest(request) || processNewForms) {
			form.processRequest(request);
		}
		return createModelAndView(form, request, response);
	}

	/**
	 * Returns the {@link Form Form} for the given request. By 
	 * default this method looks for an existing instance in the HTTP session 
	 * under the key returned by {@link #getSessionAttribute} and 
	 * returns it.
	 */
	protected Form getForm(HttpServletRequest request) {
		log.debug("Retrieving Form from session");
		HttpSession session = request.getSession();
		String attrName = getSessionAttribute(request);
		Form form = (Form) session.getAttribute(attrName);
		return form;
	}
		
	protected ModelAndView showForm(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		PrintWriter writer = getWriter(request,response);
		renderForm(form, writer);
		return null;
	}
	
	protected PrintWriter getWriter(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		return response.getWriter();		
	}
	
	protected void renderForm(Form form, PrintWriter writer) {
		form.render(writer);
	}
	
	/**
	 * Returns whether the given request is an initial form request. By
	 * default it is checked whether the request method is <tt>GET</tt> and 
	 * {@link #isContentRequest} returns <code>false</code>.  
	 */
	protected boolean isInitialRequest(HttpServletRequest request) {
		return "GET".equals(request.getMethod()) && !isContentRequest(request);
	}
	
	/**
	 * Returns whether the request is to be handled by a {@link ContentElement}. 
	 */
	protected boolean isContentRequest(HttpServletRequest request) {
		return "GET".equals(request.getMethod()) && 
				request.getParameter(CONTENT_PARAM) != null;
	}
	
	protected boolean isExclusiveRequest(HttpServletRequest request) {
		return request.getParameter(EXCLUSIVE_PARAM) != null;
	}
	
	protected ModelAndView handleContentRequest(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		if (form != null) {
			String id = request.getParameter(CONTENT_PARAM);
			Element element = form.getElementById(id);
			if (element instanceof ContentElement) {
				ContentElement ce = (ContentElement) element;
				ce.handleContentRequest(request, response);
				return null;
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}
	
	protected ModelAndView handleExclusiveRequest(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		if (form != null) {
			String id = request.getParameter(EXCLUSIVE_PARAM);
			Element element = form.getElementById(id);
			element.processRequest(request);
		}
		return null;
	}

	/**
	 * Creates and initializes a form.
	 */
	protected Form createAndInitForm(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
				
		Form form = createForm(request);
		form.setFormContext(formContextFactory.createFormContext(request, response));
		
		if (buttonFactories != null && !buttonFactories.isEmpty()) {
			Container container = form.createContainer(BUTTON_CONTAINER_ID);
			Iterator it = buttonFactories.iterator();
			while (it.hasNext()) {
				ButtonFactory buttonFactory = (ButtonFactory) it.next();
				container.addElement(buttonFactory.createButton());
			}
		}
		
		form.setValue(getFormBackingObject(request)); 
		form.init();
		initForm(form, request);
		
		String attrName = getSessionAttribute(request);
		request.getSession().setAttribute(attrName, form);
		
		return form;
	}

	protected abstract Form createForm(HttpServletRequest request);
	
	protected void initForm(Form form, HttpServletRequest request) {
	}
	
	protected final ModelAndView createModelAndView(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		FormSubmissionHandler handler = (FormSubmissionHandler) 
				form.getAttribute(FORM_SUBMISSION_HANDLER);
		
		form.setAttribute(FORM_SUBMISSION_HANDLER, null);
		
		if (handler != null && !form.hasErrors()) {
			return handler.handleFormSubmission(form, request, response);
		}
		return showForm(form, request, response);
	}
	
	protected abstract Object getFormBackingObject(HttpServletRequest request)
			throws Exception;
	
	protected String getSessionAttribute(HttpServletRequest request) {
		return AbstractFormController.class.getName() + ".form";
	}
	
}
