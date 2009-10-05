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
package org.riotfamily.forms.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;


/**
 *
 */
public abstract class AjaxFormController extends AbstractFormController
		implements MessageSourceAware {
	
	private MessageSource messageSource;
	
	public AjaxFormController(FormContextFactory formContextFactory) {
		super(formContextFactory);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	protected MessageSource getMessageSource() {
		return messageSource;
	}

	protected ModelAndView handleFormRequest(Form form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (ServletUtils.isXmlHttpRequest(request)) {
			processAjaxRequest(form, request, response);
			return null;
		}
		else {
			return super.handleFormRequest(form, request, response);
		}
	}
		
	protected boolean isEventRequest(HttpServletRequest request) {
		return request.getParameter("event.type") != null;
	}
	
	/**
	 * Returns whether the given request is an initial form request.
	 */
	protected boolean isInitialRequest(HttpServletRequest request) {
		return super.isInitialRequest(request) 
				&& !ServletUtils.isXmlHttpRequest(request);
	}
	
	protected void processAjaxRequest(Form form, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		AjaxResponse ajaxResponse = new AjaxResponse(response);
		if (form != null) {
			form.getFormContext().setWriter(response.getWriter());
			form.setFormListener(ajaxResponse);
			if (isEventRequest(request)) {
				processEventRequest(form, request);
			}
			else {
				processForm(form, request);
			}
			form.setFormListener(null);
		}
		else {
			String message = messageSource.getMessage(
					"error.sessionExpired", null, "Your session has expired", 
					RequestContextUtils.getLocale(request));
			
			ajaxResponse.alert(message);
		}
		ajaxResponse.close();
	}
	
	protected void processEventRequest(Form form, HttpServletRequest request) {
		String id = request.getParameter("event.source");		
		log.debug("Processing AJAX request triggered by element " + id);
		Element element = form.getElementById(id);
		JavaScriptEvent event = new JavaScriptEvent(request);
		if (element instanceof JavaScriptEventAdapter) {
			JavaScriptEventAdapter ea = (JavaScriptEventAdapter) element;
			ea.handleJavaScriptEvent(event);
		}
		else {
			log.error("Element does not implement JavaScriptEventAdapter");
		}
	}
	
}
