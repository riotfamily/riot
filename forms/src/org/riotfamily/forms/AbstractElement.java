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
package org.riotfamily.forms;

import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.forms.request.FormRequest;


/**
 * Convinient superclass for element implementations. 
 */
public abstract class AbstractElement implements Element {

	protected Log log = LogFactory.getLog(getClass());
	
	private Form form;

	private FormContext formContext;
	
	private Element parent;

	private String label;
	
	private String hint;
	
	private String id;
	
	private String styleClass;
	
	private boolean required;
	
	private boolean enabled = true;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getStyleClass() {
		return styleClass;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
		
	public Form getForm() {
		return form;
	}
	
	public final void setForm(Form form) {
		this.form = form;
		afterFormSet();
	}

	protected void afterFormSet() {
	}

	public FormContext getFormContext() {
		return formContext;
	}
	
	public final void setFormContext(FormContext formContext) {
		this.formContext = formContext;
		afterFormContextSet();
	}
	
	protected void afterFormContextSet() {
	}
	
	public Element getParent() {
		return parent;
	}

	public void setParent(Element parent) {
		this.parent = parent;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getHint() {
		return hint;
	}
	
	public void setHint(String hint) {
		this.hint = hint;
	}
		
	public void focus() {
		if (form == null) {
			throw new IllegalStateException(
					"Element must be registered before calling focus()");
		}
		form.requestFocus(this);
	}

	public final void render() {
		if (form == null) {
			throw new IllegalStateException("A form must be set!");
		}
		PrintWriter writer = getFormContext().getWriter();
		if (writer == null) {
			throw new IllegalStateException("A writer must be set!");
		}
		render(writer);
	}

	public void render(PrintWriter writer) {
		renderInternal(writer);
		form.elementRendered(this);
	}
	
	protected abstract void renderInternal(PrintWriter writer);

	/**
	 * Subclasses may override this method to change their internal state
	 * according the given request.
	 */
	public void processRequest(FormRequest request) {
	}
	
	/**
	 * Returns <code>true</code>, if the element as well as its parent is
	 * enabled.
	 */
	public final boolean isEnabled() {
		if (enabled && (parent != null)) {
			return parent.isEnabled();
		}
		return enabled;
	}
	
	/**
	 * Enables (or disables) the element. The state of nested elements 
	 * will be implicitly affected, since {@link #isEnabled()} takes the state
	 * of its parent element into account.
	 */
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (getFormListener() != null) {
			getFormListener().elementEnabled(this);
		}
	}
	
	/**
	 * Returns whether the element is mandatory and must be filled out by 
	 * the user.
	 */
	public boolean isRequired() {
		return required;
	}
	
	/**
	 * Sets whether the element is required.
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public boolean isCompositeElement() {
		return false;
	}
		
	protected FormListener getFormListener() {
		if (form != null) {
			return form.getFormListener();
		}
		return null;
	}
}
