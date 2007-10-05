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
package org.riotfamily.pages.mail;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.EmailValidationUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.SimpleFormController;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Simple controller that sends data collected from a from via email.
 * A FreeMarker template is used to format the mail body.
 */
public class SimpleMailFormController extends SimpleFormController 
		implements InitializingBean {

	private String to;
	
	private String[] bcc;
	
	private String from;
	
	private String subject;
	
	private MailSender mailSender;
	
	private Configuration freemarkerConfig;
	
	private String mailTemplateName;
	
	private String[] requiredFields;
	
	
	public SimpleMailFormController() {
		setCommandClass(MailForm.class);
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setFreemarkerConfig(Configuration freemarkerConfig) {
		this.freemarkerConfig = freemarkerConfig;
	}

	public void setMailTemplateName(String mailTemplateName) {
		this.mailTemplateName = mailTemplateName;
	}

	public void setRequiredFields(String[] requiredFields) {
		this.requiredFields = requiredFields;
	}	
	
	protected void onBindAndValidate(HttpServletRequest request, 
					Object command, BindException errors) throws Exception {
		
		if (command instanceof MailForm) {
			MailForm form = (MailForm) command;
			if (form.getEmail() != null && !EmailValidationUtils.isValid(form.getEmail())) {
				errors.rejectValue("email", "error.email.invalid", "E-Mail is invalid");
			}
		}
		
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(to);
		Assert.notNull(mailSender);
		Assert.notNull(freemarkerConfig);
		Assert.notNull(mailTemplateName);
	}
	
	protected void initBinder(HttpServletRequest request, 
			ServletRequestDataBinder binder) throws Exception {
		
		binder.setRequiredFields(requiredFields);
	}
	
	protected void doSubmitAction(Object command) throws Exception {
		Template template = freemarkerConfig.getTemplate(mailTemplateName);
		
		StringWriter mailTextWriter = new StringWriter();
		template.process(command, mailTextWriter);
	
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(to);
		mail.setBcc(bcc);
		mail.setFrom(from);
		mail.setSubject(subject);
		mail.setText(mailTextWriter.toString());
		mailSender.send(mail);
	}

}
