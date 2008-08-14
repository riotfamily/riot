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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.security.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.AjaxFormController;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.element.PasswordField;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.runtime.RiotRuntimeAware;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.auth.RiotUserDao;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class ChangePasswordController extends AjaxFormController 
		implements FormSubmissionHandler, RiotRuntimeAware {
	
	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
		new DefaultTransactionDefinition(
		TransactionDefinition.PROPAGATION_REQUIRED);
	
	private PlatformTransactionManager transactionManager;
	
	private RiotUserDao dao;
	
	private RiotRuntime runtime;
	
	private String viewName = ResourceUtils.getPath(
			ChangePasswordController.class, "ChangePasswordFormView.ftl");
	
	public ChangePasswordController(RiotUserDao dao, 
				PlatformTransactionManager transactionManager) {
		
		this.dao = dao;
		this.transactionManager = transactionManager;
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
	}
	
	public void setRiotRuntime(RiotRuntime runtime) {
		this.runtime = runtime;		
	}

	protected Form createForm(HttpServletRequest request) {
		
		Form form = new Form(NewPassword.class);
		PasswordField pw = new PasswordField();
		pw.setConfirm(true);
		pw.setRequired(true);
		form.addElement(pw, "password");
		return form;
	}
	
	public ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
		try {
			NewPassword newPassword = (NewPassword) form.populateBackingObject();
			dao.updatePassword(AccessController.getCurrentUser(), newPassword.getPassword());
		}
		catch (Exception e) {
				transactionManager.rollback(status);
				throw e;
		}
		transactionManager.commit(status);
		String url = request.getContextPath() + runtime.getServletPrefix() + "/group";
		return new ModelAndView(new RedirectView(url));
	}
	
	@Override
	protected ModelAndView showForm(Form form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		return new ModelAndView(viewName, "form", sw.toString());
	}
	
	public static class NewPassword {
		
		private String password;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
	}

	

}
