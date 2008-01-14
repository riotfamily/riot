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
package org.riotfamily.components.editor;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.transaction.TransactionalHandler;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.forms.factory.RepositoryFormController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractFrontOfficeFormController 
		extends RepositoryFormController 
		implements FormSubmissionHandler, TransactionalHandler {

	private static final String SESSION_ATTRIBUTE = "frontOfficeForm";

	private String viewName = ResourceUtils.getPath(
			AbstractFrontOfficeFormController.class, "ComponentFormView.ftl");

	private String successViewName = ResourceUtils.getPath(
			AbstractFrontOfficeFormController.class, "ComponentFormSuccessView.ftl");

	private String formIdAttribute = "formId";

	public AbstractFrontOfficeFormController(FormRepository formRepository) {
		super(formRepository);
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setSuccessViewName(String successViewName) {
		this.successViewName = successViewName;
	}

	protected String getFormId(HttpServletRequest request) {
		return (String) request.getAttribute(formIdAttribute);
	}

	protected String getSessionAttribute(HttpServletRequest request) {
		return SESSION_ATTRIBUTE;
	}

	protected ModelAndView showForm(final Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		return new ModelAndView(viewName, "form", sw.toString());
	}

	public ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		onSave(form.populateBackingObject(), request);
		return new ModelAndView(successViewName);
	}
	
	protected abstract void onSave(Object object, HttpServletRequest request);

}