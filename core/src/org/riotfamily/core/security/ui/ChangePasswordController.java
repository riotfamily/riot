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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.core.screen.list.command.impl.dialog.DialogFormController;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUserDao;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.element.PasswordField;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;

public class ChangePasswordController extends DialogFormController {
	
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
		
		Button button = new Button();
		button.setParamName("save");
		button.setLabelKey("label.form.button.save");
		
		button.addClickListener(new ClickListener() {
			
			public void clicked(ClickEvent event) {
				Form form = event.getSource().getForm();
				Locale locale = form.getFormContext().getLocale();
				String resourcePath = form.getFormContext().getContextPath() + form.getFormContext().getResourcePath();
				String icon = resourcePath + "style/images/icons/save.png";
				String title = getMessageSource().getMessage("notification.changedPassword.title", null,"Change Password", locale);
				String message = getMessageSource().getMessage("notification.changedPassword.message", null,"Password has been changed", locale);
				if (!form.hasErrors()) {
					changePassword(form);
					form.getFormListener().eval(
							String.format("parent.riot.notification.show({title : '%s', icon : '%s', message: '%s'});parent.riot.window.closeAll();", title, icon, message)
					);
				}	
			}
		});
		button.setPartitialSubmit(form.getId());
		form.addButton(button);
		return form;
	}
	
	@Override
	protected String getSessionAttribute(HttpServletRequest request) {
		return "changePassword";
	}
	
	@Override
	protected ModelAndView showForm(Form form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		return new ModelAndView(viewName, "form", sw.toString());
	}
	
	protected void changePassword(Form form) {
		
		TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
		try {
			NewPassword newPassword = (NewPassword) form.populateBackingObject();
			dao.updatePassword(AccessController.getCurrentUser(), newPassword.getPassword());
		}
		catch (Exception e) {
				transactionManager.rollback(status);
				log.error("Error changing password", e);
		}
		transactionManager.commit(status);
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
