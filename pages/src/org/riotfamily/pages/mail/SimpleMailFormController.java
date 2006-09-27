package org.riotfamily.pages.mail;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.Assert;
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
