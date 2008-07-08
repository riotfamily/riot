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
package org.riotfamily.website.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.bind.MapServletRequestDataBinder;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Simple controller that sends data collected from a from via email.
 * A FreeMarker template is used to format the mail body.
 */
public class SimpleMailFormController extends SimpleFormController 
		implements InitializingBean {
	
	private Pattern labelPattern = Pattern.compile("\\|([^\\|]*)\\|([^\\|]*)\\|");

	private String to;
	
	private String[] bcc;
	
	private String from;
	
	private String subject;
	
	private MailSender mailSender;
	
	private String[] requiredFields;

	
	public SimpleMailFormController() {
		setCommandClass(HashMap.class);
	}
	
	@Override
	protected ServletRequestDataBinder createBinder(
			HttpServletRequest request, Object command) throws Exception {
		
		ServletRequestDataBinder binder = new MapServletRequestDataBinder((Map <?, ?>) command, getCommandName());
		prepareBinder(binder);
		initBinder(request, binder);
		return binder;
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

	public void setRequiredFields(String[] requiredFields) {
		this.requiredFields = requiredFields;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(to);
		Assert.notNull(mailSender);
	}
	
	protected void initBinder(HttpServletRequest request, 
			ServletRequestDataBinder binder) throws Exception {
		
		binder.setRequiredFields(requiredFields);
	}
	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(to);
		mail.setBcc(bcc);
		mail.setFrom(from);
		mail.setSubject(subject);
		
		mail.setText(getMailText(request, command));
		prepareMail(mail, command);
		mailSender.send(mail);
		
		String url = ServletUtils.getOriginatingRequestUri(request) + "?success=true"; 
		return new ModelAndView(new RedirectView(url));
	}
	
	
	@SuppressWarnings("unchecked")
	protected String getMailText(HttpServletRequest request, Object command) {		

		Map<String, String> labels = new HashMap<String, String>();
		
		String fieldString = request.getParameter("_fields");
		Matcher m = labelPattern.matcher(fieldString);		
		while (m.find()) {
			labels.put(m.group(1), m.group(2));			
		}
		
		String[] fields = labelPattern.matcher(fieldString).replaceAll("").split(",");
		
		Map<String, String> values = (Map<String, String>) command;
		StringBuffer mailText = new StringBuffer();		
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			if (labels.containsKey(field)) {
				if (i > 0) {
					mailText.append("\n\n");
				}		
				mailText.append(labels.get(field))
				  	    .append(':');				  
			}
			String value = values.get(field);
			if (value == null) {
				value = "-";
			}
			mailText.append(' ').append(value);
		}	
		
		return mailText.toString();
	}
	
	/**
	 * This method can be overriden in order to manipulate the mail before it
	 * is being sent.
	 */
	protected void prepareMail(SimpleMailMessage mail, Object command) {
	}
	
}
