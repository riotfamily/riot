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
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.FormContext;
import org.riotfamily.forms.FormListener;
import org.riotfamily.forms.event.EventPropagation;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.LoadingCodeGenerator;
import org.riotfamily.forms.resource.ResourceElement;


/**
 * FormListener implementation used by the 
 * {@link org.riotfamily.forms.controller.AjaxFormController AjaxFormController} to
 * notify the client of structural changes. It creates a JSON response that
 * contains the modifications to be performed on the client side DOM.
 */
public class AjaxResponse implements FormListener {

	private Log log = LogFactory.getLog(AjaxResponse.class);
	
	private PrintWriter writer;
	
	private LinkedHashSet resources = new LinkedHashSet();
	
	private List propagations = new LinkedList();
	
	private List dhtmlElements = new LinkedList();
	
	private HashSet validatedElements = new HashSet();
	
	private Element focusedElement;
	
	private JSONArray json = new JSONArray();
	
	public AjaxResponse(HttpServletResponse response) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		ServletUtils.setNoCacheHeaders(response);
		this.writer = response.getWriter();
	}
	
	private String renderElement(Element element) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		FormContext context = element.getForm().getFormContext();
		context.setWriter(pw);
		element.render(pw);
		context.setWriter(writer);
		return sw.toString();
	}
	
	public void elementChanged(Element element) {
		json.add(new Action("replace", element.getId(), renderElement(element)));
		validatedElements.add(element);
	}

	public void elementValidated(Element element) {
		validatedElements.add(element);
	}
	
	public void elementRemoved(Element element) {
		json.add(new Action("remove", element.getId()));
	}

	public void elementAdded(Element element) {
		json.add(new Action("insert", element.getParent().getId(), renderElement(element)));
	}
	
	public void elementFocused(Element element) {
		log.debug("Focus requested for: " + element.getId());
		focusedElement = element;
	}
	
	public void elementEnabled(Element element) {
		json.add(new Action("enable", element.getId(), String.valueOf(element.isEnabled())));
	}
	
	public void elementRendered(Element element) {
		log.debug("Element rendered: " + element);
		if (element instanceof JavaScriptEventAdapter) {
			JavaScriptEventAdapter adapter = (JavaScriptEventAdapter) element;
			EventPropagation.addPropagations(adapter, propagations);
		}
		if (element instanceof ResourceElement) {
			ResourceElement re = (ResourceElement) element;
			FormResource res = re.getResource();
			if (res != null) {
				resources.add(res);
			}
		}
		if (element instanceof DHTMLElement) {
			log.debug("DHTML element registered");
			dhtmlElements.add(element);
		}
	}
	
	public void refresh(Element element) {
		json.add(new Action("refresh", element.getId()));
	}
	
	public void alert(String message) {
		json.add(new Action("eval", null, "alert('" + message + "');"));
	}
	
	private void renderPropagations() {
		Iterator it = propagations.iterator();
		while (it.hasNext()) {
			EventPropagation p = (EventPropagation) it.next();
			renderPropagation(p);
		}
	}
	
	private void renderPropagation(EventPropagation propagation) {
		log.debug("Propagating " + propagation.getType() + 
				" events for element " + propagation.getId());
		
		json.add(new Action("propagate", propagation.getId(), propagation.getType())); 
	}
	
	private void renderScripts() {
		Iterator it = dhtmlElements.iterator();
		while (it.hasNext()) {
			DHTMLElement e = (DHTMLElement) it.next();
			String script = e.getInitScript();
			if (script != null) {
				log.debug("Evaluating init script ...");
				if (e instanceof ResourceElement) {
					ResourceElement resEle = (ResourceElement) e;
					FormResource res = resEle.getResource();
					StringBuffer sb = new StringBuffer();
					if (res != null) {
						sb.append("Resources.execWhenLoaded(['");
						sb.append(res.getUrl());
						sb.append("'], function() {");
						sb.append(script);
						sb.append("})");
						script = sb.toString();
					}
				}
				json.add(new Action("eval", null, script));
			}
		}
	}
	
	private void renderResources() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		LoadingCodeGenerator.renderLoadingCode(resources, pw);
		json.add(new Action("eval", null, sw.toString()));
	}
	
	private void renderFocus() {
		if (focusedElement != null) {
			log.debug("Focusing element " + focusedElement.getId());
			json.add(new Action("focus", focusedElement.getId()));
		}
	}
	
	private void renderErrors() {
		Iterator it = validatedElements.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element.getForm().getErrors().getErrors(element) != null) {
				boolean valid = !element.getForm().getErrors().hasErrors(element);
				json.add(new Action("valid", element.getId(), String.valueOf(valid)));
				
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				FormContext context = element.getForm().getFormContext();
				context.setWriter(pw);
				element.getForm().getErrors().renderErrors(element);
				context.setWriter(writer);
				json.add(new Action("error", element.getId(), sw.toString()));
			}
		}
	}
	
	private void onClose() {
		renderResources();
		renderFocus();
		renderPropagations();
		renderScripts();
		renderErrors();
	}
	
	public void close() {
		onClose();
		writer.print(json.toString());
	}
	
	public static class Action {
		
		private String command;
		
		private String element;
		
		private String value;

		public Action(String command, String element) {
			this(command, element, null);
		}
		
		public Action(String command, String element, String value) {
			this.command = command;
			this.element = element;
			this.value = value;
		}

		public String getCommand() {
			return this.command;
		}

		public String getElement() {
			return this.element;
		}

		public String getValue() {
			return this.value;
		}

	}
	
}
