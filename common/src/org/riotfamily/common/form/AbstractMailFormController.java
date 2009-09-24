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
package org.riotfamily.common.form;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.bind.MapServletRequestDataBinder;
import org.riotfamily.common.servlet.ServletUtils;
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
 */
public abstract class AbstractMailFormController extends SimpleFormController 
		implements InitializingBean {
	
	private String to;
	
	private String[] bcc;
	
	private String from;
	
	private String subject;
	
	private MailSender mailSender;
	
	private String[] requiredFields;

	
	public AbstractMailFormController() {
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
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(to);
		mail.setBcc(bcc);
		mail.setFrom(from);
		mail.setSubject(subject);
		
		Map<String, String> data = (Map<String, String>) command;
		mail.setText(getMailText(request, data));
		prepareMail(mail, data);
		mailSender.send(mail);
		
		String url = ServletUtils.getOriginatingRequestUri(request) + "?success=true"; 
		return new ModelAndView(new RedirectView(url));
	}
	
	protected abstract String getMailText(HttpServletRequest request, 
			Map<String, String> data) throws Exception;
	
	/**
	 * This method can be overridden in order to manipulate the mail before it
	 * is being sent.
	 */
	protected void prepareMail(SimpleMailMessage mail, Map<String, String> data) {
	}
	
}
