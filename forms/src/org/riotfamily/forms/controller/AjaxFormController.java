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
package org.riotfamily.forms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.event.EventPropagation;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
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
	
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
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
	
	protected void initForm(Form form, HttpServletRequest request) {
		form.addResource(new ScriptResource("form/ajax.js", "propagate", 
				Resources.PROTOTYPE));
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
	
	protected void renderForm(Form form, PrintWriter writer) {
		form.render(writer);
		writer.print("<script>");
		ArrayList propagations = new ArrayList();
		Iterator it = form.getRegisteredElements().iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element instanceof JavaScriptEventAdapter) {
				JavaScriptEventAdapter adapter = (JavaScriptEventAdapter) element;
				EventPropagation.addPropagations(adapter, propagations);
			}
		}
		
		if (!propagations.isEmpty()) {
			writer.print("Resources.waitFor('propagate', function() {");
			it = propagations.iterator();
			while (it.hasNext()) {
				EventPropagation p = (EventPropagation) it.next();
				writer.print("propagate('");
				writer.print(p.getId());
				writer.print("', '");
				writer.print(p.getType());
				writer.print("');\n");
			}
			writer.print("});");
		}
		writer.print("</script>");
	}
	
}
