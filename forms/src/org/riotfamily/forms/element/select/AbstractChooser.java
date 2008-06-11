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
package org.riotfamily.forms.element.select;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.AbstractEditorBase;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for elements that let the user choose a reference to
 * to another object.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractChooser extends AbstractEditorBase
		implements Editor, DHTMLElement, JavaScriptEventAdapter, 
		ResourceElement, ContentElement {
	
	private Object object;

	private List listeners;
	
	private static FormResource RESOURCE = 
			new ScriptResource("form/chooser.js", "Chooser");
	
	
	protected void renderInternal(PrintWriter writer) {
		DocumentWriter doc = new DocumentWriter(writer);
		doc.start(Html.DIV)
				.attribute(Html.COMMON_ID, getId())
				.attribute(Html.COMMON_CLASS, "chooser")
				.body();
				
		renderLabel(object, writer);
		
		doc.start(Html.BUTTON).attribute(Html.COMMON_CLASS, "choose");
		doc.body("Choose");
		doc.end();
		
		if (!isRequired() && getValue() != null) {
			doc.start(Html.BUTTON);
			doc.attribute(Html.COMMON_CLASS, "unset");
			doc.body("Unset");
			doc.end();
		}
		doc.end();
	}

	protected abstract void renderLabel(Object object, PrintWriter writer);
	
	public int getEventTypes() {
		return 0;
	}
	
	public void processRequest(FormRequest request) {
		validate();
	}
	
	protected void validate() {
		if (isRequired() && object == null) {
			ErrorUtils.reject(this, "required");
		}
	}

	public void handleJavaScriptEvent(JavaScriptEvent event) {
		setObjectId(event.getValue());
		getFormListener().elementChanged(this);
	}
	
	public FormResource getResource() {
		return RESOURCE;
	}
	
	public String getInitScript() {
		return "new Chooser('" + getId() + "');";
	}
	
	protected abstract Object loadBean(String objectId);
	
	protected void setObjectId(String objectId) {
		log.debug("Setting objectId to: " + objectId);
		Object oldObject = object;
		if (StringUtils.hasLength(objectId)) {
			object = loadBean(objectId);
		}
		else {
			object = null;
		}
		fireChangeEvent(object, oldObject);
	}
	
	public void setValue(Object value) {
		this.object = value;
	}

	public Object getValue() {
		return object;
	}
	
	public void handleContentRequest(HttpServletRequest request, 
			HttpServletResponse response) throws IOException {

		response.setContentType("text/html");
		Map model = new HashMap();
		String pathUrl = getPathUrl();
		if (pathUrl != null) {
			model.put("pathUrl", request.getContextPath() + pathUrl);
		}
		model.put("chooserUrl", request.getContextPath() + getChooserUrl());
		String template = TemplateUtils.getTemplatePath(AbstractChooser.class, 
				"_content");
		
		getFormContext().getTemplateRenderer().render(template, 
				model, response.getWriter());
	}
	
	protected abstract String getChooserUrl();
	
	protected abstract String getPathUrl();
	
}
