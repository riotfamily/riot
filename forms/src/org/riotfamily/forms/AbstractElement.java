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
package org.riotfamily.forms;

import java.io.PrintWriter;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.TagWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.riotfamily.forms.request.FormRequest;


/**
 * Convenient superclass for element implementations. 
 */
public abstract class AbstractElement implements Element {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	private Form form;

	private FormContext formContext;
	
	private Element parent;

	private String label;
	
	private String hint;
	
	private String id;
	
	private String styleClass;
	
	private boolean required;
	
	private boolean enabled = true;
	
	private boolean visible = true;
	
	private boolean wrap = true;
	
	private boolean inline = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getEventTriggerId() {
		return id;
	}
	
	public String getStyleClass() {
		return FormatUtils.join(" ", styleClass, getSystemStyleClass(), 
				isEnabled() ? null : "disabled");
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	protected String getSystemStyleClass() {
		return null;
	}
	
	protected String getWrapperStyleClass() {
		if (getSystemStyleClass() != null) {
			return getSystemStyleClass() + "-wrapper";
		}
		return null;
	}
	
	protected void setWrap(boolean wrap) {
		this.wrap = wrap;
	}
	
	protected void setInline(boolean inline) {
		this.inline = inline;
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

	public final void render(PrintWriter writer) {
		if (wrap) {
			TagWriter wrapper = new TagWriter(writer);
			wrapper.start(inline ? "span" : "div");
			wrapper.attribute("id", getId());
			if (getWrapperStyleClass() != null) {
				wrapper.attribute("class", getWrapperStyleClass());
			}
			wrapper.body();
			if (isVisible()) {
				renderInternal(writer);
			}
			wrapper.closeAll();
		}
		else {			
			renderInternal(writer);
		}		
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
	public boolean isEnabled() {
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
	 * Setting an element to read-only is the same as invoking
	 * {@link #setEnabled(boolean) setEnabled(false)}.
	 */
	public void setReadOnly(boolean readOnly) {
		setEnabled(!readOnly);
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
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		boolean toggled = this.visible != visible; 
		this.visible = visible;
		if (toggled && getFormListener() != null) {
			getFormListener().elementChanged(this);
		}
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
