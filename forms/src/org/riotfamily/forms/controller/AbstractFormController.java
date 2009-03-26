/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 * Abstract base class for controllers that display a form.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractFormController implements Controller {

	public static final String FORM_SUBMISSION_HANDLER = "formSubmissionHandler";
	
	protected static final String BUTTON_CONTAINER_ID = "buttons";
	
	private static final String CONTENT_PARAM = "_content";
	
	private static final String EXCLUSIVE_PARAM = "_exclusive";
	
	protected RiotLog log = RiotLog.get(getClass());
	
	private FormContextFactory formContextFactory;
	
	private boolean processNewForms;
	
	public AbstractFormController(FormContextFactory formContextFactory) {
		this.formContextFactory = formContextFactory;
	}

	public final void setProcessNewForms(boolean processNewForms) {
		this.processNewForms = processNewForms;
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
		else {
			return handleFormRequest(form, request, response);
		}
	}
	
	protected ModelAndView handleFormRequest(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		if (!isInitialRequest(request) || processNewForms) {
			processForm(form, request);
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
		return request.getParameter(CONTENT_PARAM) != null;
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
	
	/**
	 * Creates and initializes a form.
	 */
	protected Form createAndInitForm(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
				
		Form form = createForm(request);
		form.setFormContext(formContextFactory.createFormContext(request, response));
		
		populateForm(form, request); 
		form.init();
		initForm(form, request);
		
		String attrName = getSessionAttribute(request);
		request.getSession().setAttribute(attrName, form);
		
		return form;
	}
	
	protected void processForm(Form form, HttpServletRequest request) {
		if (isExclusiveRequest(request)) {
			String id = request.getParameter(EXCLUSIVE_PARAM);
			form.processExclusiveRequest(id, request);
		}
		else {
			form.processRequest(request);
		}
	}
	
	/**
	 * Populates newly created forms. The default implementation invokes 
	 * {@link Form#setValue(Object)} with the object returned by 
	 * {@link #getFormBackingObject(HttpServletRequest)}. 
	 */
	protected void populateForm(Form form, HttpServletRequest request) 
			throws Exception {
		
		form.setBackingObject(getFormBackingObject(request));
	}
	
	/**
	 * Returns the object backing the form. Subclasses may overwrite this method
	 * to retrieve a persistent object. The default implementation returns
	 * <code>null</code>.
	 */
	protected Object getFormBackingObject(HttpServletRequest request) 
			throws Exception {
		
		return null;
	}

	/**
	 * Subclasses must implement this method and return a fresh 
	 * {@link Form} instance.
	 */
	protected abstract Form createForm(HttpServletRequest request);
	
	/**
	 * Subclasses may overwrite this method to initialize forms after they have
	 * been populated. The default implementation does nothing.
	 */
	protected void initForm(Form form, HttpServletRequest request) {
	}
	
	protected final ModelAndView createModelAndView(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		if (isExclusiveRequest(request)) {
			return null;
		}
		if (form.getClickedButton() != null && !form.hasErrors()) {
			return handleFormSubmission(form, request, response);
		}
		return showForm(form, request, response);
	}
	
	protected abstract ModelAndView handleFormSubmission(Form form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;
	
	/**
	 * Returns the name of the attribute under which the {@link Form} is
	 * stored in the HTTP session. 
	 */
	protected String getSessionAttribute(HttpServletRequest request) {
		return AbstractFormController.class.getName() + ".form";
	}
	
	/**
	 * Removes the Form from the HTTP session.
	 */
	protected void removeFormFromSession(HttpServletRequest request) {
		request.getSession().removeAttribute(getSessionAttribute(request));
	}
}
