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
package org.riotfamily.forms.element;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.TemplateUtils;


/**
 * CompositeElement that is rendered using a template.
 */
public class TemplateElement extends CompositeElement {
	
	private Map renderModel = new HashMap();
	
	private String template;
	
	public TemplateElement() {
		this("element");
	}
	
	public TemplateElement(String modelKey) {
		template = TemplateUtils.getTemplatePath(this);
		setAttribute(modelKey, this);
	}
		
	protected void addComponent(String key, Element element) {
		addComponent(element);
		setAttribute(key, element);
	}
	
	public Map getRenderModel() {
		return renderModel;
	}
	
	public void setAttribute(String key, Object value) {
		renderModel.put(key,value);
	}
	
	public Object getAttribute(String key) {
		return renderModel.get(key);
	}
	
	/**
	 * Returns the name of the template that is used to render the element.
	 */
	protected final String getTemplate() {
		return template;
	}

	/**
	 * Sets the name of the template that is used to render the element.
	 * 
	 * @param name name of the template to use
	 * @see #renderInternal(PrintWriter)
	 * @see org.riotfamily.forms.TemplateRenderer
	 */
	public final void setTemplate(String name) {
		this.template = name;
	}
	
	protected void afterFormContextSet() {
		setAttribute("messageResolver", getFormContext().getMessageResolver());
		setAttribute("errors", getForm().getErrors());
	}
	
	protected void renderComponents(PrintWriter writer) {
		renderTemplate(writer);
	}
	
	protected void renderTemplate(PrintWriter writer) {
		getFormContext().getTemplateRenderer().render(
				template, renderModel, writer);
	}
}
