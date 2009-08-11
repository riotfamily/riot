package org.riotfamily.forms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.RiotLog;
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

	private RiotLog log = RiotLog.get(AjaxResponse.class);
	
	private PrintWriter writer;
	
	private LinkedHashSet<FormResource> resources = Generics.newLinkedHashSet();
	
	private List<EventPropagation> propagations = Generics.newLinkedList();
	
	private List<DHTMLElement> dhtmlElements = Generics.newLinkedList();
	
	private HashSet<Element> validatedElements = Generics.newHashSet();
	
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
		json.add(new Action("setVisible", element.getId(), String.valueOf(element.isVisible())));
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
		json.add(new Action("setVisible", element.getId(), String.valueOf(element.isVisible())));
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
			dhtmlElements.add((DHTMLElement) element);
		}
	}
	
	public void refresh(Element element) {
		json.add(new Action("refresh", element.getId()));
	}
	
	public void alert(String message) {
		eval("alert('" + message + "');");
	}
	
	public void eval(String script) {
		json.add(new Action("eval", null, script));
	}
	
	private void renderPropagations() {
		for (EventPropagation p : propagations) {
			renderPropagation(p);
		}
	}
	
	private void renderPropagation(EventPropagation propagation) {
		log.debug("Propagating " + propagation.getType() + 
				" events for element " + propagation.getTriggerId());
		
		json.add(new Action("propagate", propagation.getSourceId(), propagation.getType(), propagation.getTriggerId())); 
	}
	
	private void renderScripts() {
		for (DHTMLElement e : dhtmlElements) {
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
		for (Element element : validatedElements) { 
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
		
		private String trigger;

		public Action(String command, String element) {
			this(command, element, null);
		}
		
		public Action(String command, String element, String value) {
			this(command, element, value, null);
		}
		
		public Action(String command, String element, String value, String trigger) {
			this.command = command;
			this.element = element;
			this.value = value;
			this.trigger = trigger;
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
		
		public String getTrigger() {
			return trigger;
		}

	}
	
}
