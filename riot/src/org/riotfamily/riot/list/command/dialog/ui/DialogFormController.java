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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.command.dialog.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.AjaxFormController;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.dialog.DialogCommand;
import org.riotfamily.riot.list.ui.ListSession;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class DialogFormController extends AjaxFormController
		implements FormSubmissionHandler {
	
	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
			new DefaultTransactionDefinition(
			TransactionDefinition.PROPAGATION_REQUIRED);
		
	private ListRepository listRepository;
	
	private String commandIdAttribute = "commandId";
	
	private PlatformTransactionManager transactionManager;
	
	private String viewName = ResourceUtils.getPath(
			DialogFormController.class, "DialogFormView.ftl");
	
	public DialogFormController(ListRepository listRepository,
			PlatformTransactionManager transactionManager) {
		
		this.listRepository = listRepository;
		this.transactionManager = transactionManager;
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.dialog.button.execute");
		buttonFactory.setCssClass("button button-execute");
		addButton(buttonFactory);
	}

	
	protected String getCommandId(HttpServletRequest request) {
		return (String) request.getAttribute(commandIdAttribute);
	}
	
	protected DialogCommand getCommand(HttpServletRequest request) {
		return (DialogCommand) listRepository.getCommand(getCommandId(request));
	}
	
	
	/**
	 * Delegates the form creation to the DialogCommand.
	 */
	protected Form createForm(HttpServletRequest request) {
		return getForm(request);
	}
	
	/**
	 * Overwrites the super implementation to do nothing since the form
	 * created by the command is already populated by contract.
	 */
	protected void populateForm(Form form, HttpServletRequest request) {
	}
	
	protected String getSessionAttribute(HttpServletRequest request) {
		return getCommand(request).getFormSessionAttribute();
	}
	
	protected String getTitle(Form form, HttpServletRequest request) {
		String commandId = getCommandId(request);
		return form.getFormContext().getMessageResolver().getMessage(
				"label.dialog." + commandId, commandId);
	}
	
	protected ModelAndView showForm(final Form form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("form", sw.toString());
		model.put("title", getTitle(form, request));
		return new ModelAndView(viewName, model);
	}

	
	public ModelAndView handleFormSubmission(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		ModelAndView modelAndView;
		TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
		try {
			Object input = form.populateBackingObject();
			String key = ServletUtils.getRequiredStringAttribute(request, "listSessionKey");
			ListSession listSession = ListSession.getListSession(request, key);			
			modelAndView = getCommand(request).handleInput(input, listSession);
		}
		catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}
		transactionManager.commit(status);
		if (modelAndView == null) {
			String listUrl = (String) form.getAttribute("listUrl");
			modelAndView = new ModelAndView(new RedirectView(listUrl, true));
		}
		return modelAndView;
	}	
	
}
