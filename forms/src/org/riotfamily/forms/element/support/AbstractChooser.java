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
package org.riotfamily.forms.element.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.forms.FormRequest;
import org.riotfamily.forms.ajax.JavaScriptEvent;
import org.riotfamily.forms.ajax.JavaScriptEventAdapter;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.element.ContentElement;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.support.TemplateUtils;
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
	
	private String displayNameProperty;
	
	private Object object;

	private String displayName;

	private Collection resources = Collections.singleton(
			new ScriptResource("form/chooser.js", "Chooser"));
	
	
	public void setDisplayNameProperty(String displayNameProperty) {
		this.displayNameProperty = displayNameProperty;
	}

	protected void renderInternal(PrintWriter writer) {
		DocumentWriter doc = new DocumentWriter(writer);
		doc.start(Html.DIV);
		doc.attribute(Html.COMMON_ID, getId());
		doc.attribute(Html.COMMON_CLASS, "chooser");
		if (displayName != null) {
			doc.start(Html.SPAN);
			doc.body(displayName);
			doc.end();
		}
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
	
	public Collection getResources() {
		return resources;
	}
	
	public String getInitScript() {
		return "new Chooser('" + getId() + "');";
	}
	
	public String getPrecondition() {
		return "Chooser";
	}

	protected abstract Object loadBean(String objectId);
	
	protected String getDisplayName(Object object) {
		if (object == null) {
			return null;	
		}
		if (displayNameProperty != null) {
			return PropertyUtils.getPropertyAsString(
					object, displayNameProperty);
		}
		return object.toString();
	}
	
	protected void setObjectId(String objectId) {
		log.debug("Setting objectid to: " + objectId);
		if (StringUtils.hasLength(objectId)) {
			object = loadBean(objectId);
		}
		else {
			object = null;
		}
		displayName = getDisplayName(object);
	}
	
	public void setValue(Object value) {
		this.object = value;
		displayName = getDisplayName(value);
	}

	public Object getValue() {
		return object;
	}
	
	public void handleContentRequest(HttpServletRequest request, 
			HttpServletResponse response) throws IOException {

		response.setContentType("text/html");
		Map model = new HashMap();
		model.put("chooserUrl", request.getContextPath() + getChooserUrl());
		String template = TemplateUtils.getTemplatePath(AbstractChooser.class, 
				"_content");
		
		getFormContext().getTemplateRenderer().render(template, 
				model, response.getWriter());
	}
	
	protected abstract String getChooserUrl();
}
