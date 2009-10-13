/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.security.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mvc.view.NamedHandlerRedirectView;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUserDao;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.AjaxFormController;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.element.PasswordField;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;

public class ChangePasswordController extends AjaxFormController {
	
	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
		new DefaultTransactionDefinition(
		TransactionDefinition.PROPAGATION_REQUIRED);
	
	private PlatformTransactionManager transactionManager;
	
	private RiotUserDao dao;
		
	private String viewName = ResourceUtils.getPath(
			ChangePasswordController.class, "ChangePasswordFormView.ftl");
	
	
	public ChangePasswordController(FormContextFactory formContextFactory,
			RiotUserDao dao, PlatformTransactionManager transactionManager) {

		super(formContextFactory);
		this.dao = dao;
		this.transactionManager = transactionManager;
	}
	
	protected Form createForm(HttpServletRequest request) {
		Form form = new Form(NewPassword.class);
		PasswordField pw = new PasswordField();
		pw.setConfirm(true);
		pw.setRequired(true);
		form.addElement(pw, "password");
		form.addButton("save");
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
		return new ModelAndView(new NamedHandlerRedirectView("start"));
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
