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
import org.riotfamily.core.dao.InvalidPropertyValueException;
import org.riotfamily.core.dao.RiotDaoException;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.forms.factory.RepositoryFormController;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractFrontOfficeFormController 
		extends RepositoryFormController {

	private static final String SESSION_ATTRIBUTE = "frontOfficeForm";

	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
			new DefaultTransactionDefinition(
			TransactionDefinition.PROPAGATION_REQUIRED);
	
	private PlatformTransactionManager transactionManager;
	
	private String viewName = ResourceUtils.getPath(
			AbstractFrontOfficeFormController.class, "ComponentFormView.ftl");

	private String successViewName = ResourceUtils.getPath(
			AbstractFrontOfficeFormController.class, "ComponentFormSuccessView.ftl");

	private String formIdAttribute = "formId";

	
	public AbstractFrontOfficeFormController(
			FormContextFactory formContextFactory, 
			FormRepository formRepository,
			PlatformTransactionManager transactionManager) {
		
		super(formContextFactory, formRepository);
		this.transactionManager = transactionManager;
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

	@Override
	protected void initForm(Form form, HttpServletRequest request) {
		super.initForm(form, request);
		form.addButton("save");
	}
	
	protected ModelAndView showForm(final Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		return new ModelAndView(viewName, "form", sw.toString());
	}

	public final ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			handleFormSubmissionInternal(form, request, response);
			return new ModelAndView(successViewName);
		}
		catch (InvalidPropertyValueException e) {
			form.getErrors().rejectValue(e.getField(), e.getCode(),
					e.getArguments(), e.getMessage());

			return showForm(form, request, response);
		}
		catch (RiotDaoException e) {
			form.getErrors().reject(e.getCode(), e.getArguments(), e.getMessage());
			return showForm(form, request, response);
		}
	}

	protected void handleFormSubmissionInternal(Form form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
		try {
			Object bean = form.getBackingObject();
			reattach(bean, request);
			form.setBackingObject(bean);
			form.populateBackingObject();
			update(bean, request);
		}
		catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}
		transactionManager.commit(status);
	}
	
	protected abstract void reattach(Object object, HttpServletRequest request);
	
	protected abstract void update(Object object, HttpServletRequest request);

}