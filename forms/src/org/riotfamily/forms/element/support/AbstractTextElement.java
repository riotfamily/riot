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

import java.beans.PropertyEditor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.PasswordGenerator;
import org.riotfamily.forms.FormRequest;
import org.riotfamily.forms.ajax.JavaScriptEvent;
import org.riotfamily.forms.ajax.JavaScriptEventAdapter;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * Abstract base class for elements that handle textual input from a single HTTP
 * parameter. Optionally a <code>PropertyEditor</code> can be set to convert
 * the text into an arbitrary object.
 *
 * @see org.riotfamily.forms.bind.EditorBinder#bind(Editor, String)
 */
public abstract class AbstractTextElement extends AbstractEditorBase
		implements Editor, JavaScriptEventAdapter {

	private String type = "text";

	private Integer maxLength;

	private boolean trim = true;

	private boolean randomParamName;

	private List listeners;

	private String text;

	private PropertyEditor propertyEditor;

	private boolean validateOnChange = false;

	public AbstractTextElement() {
	}

	public AbstractTextElement(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Overrides {@link AbstractElement#getStyleClass()} and returns the
	 * element's type if no custom style has been set.
	 *
	 * @see #getType()
	 */
	public String getStyleClass() {
		String styleClass = super.getStyleClass();
		return styleClass != null ? styleClass : type;
	}

	public Integer getMaxLength() {
		return this.maxLength;
	}

	/**
	 * Sets the maximum string length.
	 */
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public String getText() {
		return text;
	}

	/**
	 * Sets the element's text value. If {@link #setTrim(boolean)} is set to
	 * <code>true</code>, leading and trailing whitespaces are stripped.
	 */
	public void setText(String text) {
		if (trim && text != null) {
			this.text = text.trim();
		}
		else {
			this.text = text;
		}
	}

	/**
	 * Sets whether the user input should be trimmed.
	 */
	public void setTrim(boolean trim) {
		this.trim = trim;
	}

	/**
	 * Sets whether a random (nonce) word should be used as parameter name
	 * to disable the browser's autocompletion feature.
	 */
	public void setRandomParamName(boolean randomParamName) {
		//REVIST Consider using setAttribute("autocomlete", "off") instead
		this.randomParamName = randomParamName;
	}

	protected String getDesiredParamName() {
		if (randomParamName) {
			return PasswordGenerator.getDefaultInstance().generate();
		}
		return super.getDesiredParamName();
	}

	public final void setPropertyEditor(PropertyEditor propertyEditor) {
		this.propertyEditor = propertyEditor;
	}

	protected final PropertyEditor getPropertyEditor() {
		return propertyEditor;
	}

	protected void initPropertyEditor() {
		if (propertyEditor == null) {
			this.propertyEditor = getEditorBinding().getPropertyEditor();
		}
	}

	protected void afterBindingSet() {
		initPropertyEditor();
	}

	public Object getValue() {
		String txt = getText();
		if (txt != null && StringUtils.hasLength(txt)
				&& propertyEditor != null) {

			try {
				propertyEditor.setAsText(txt);
				return propertyEditor.getValue();
			}
			catch (IllegalArgumentException e) {
			}
		}
		return txt;
	}

	public void setValue(Object value) {
		if (value == null) {
			return;
		}
		if (value instanceof String) {
			setText((String) value);
		}
		else {
			if (propertyEditor == null) {
				initPropertyEditor();
				Assert.notNull(propertyEditor, "Can't handle value of type "
						+ value.getClass().getName() + " - no PropertyEditor "
						+ "present");
			}
			propertyEditor.setValue(value);
			setText(propertyEditor.getAsText());
		}
	}

	public void processRequest(FormRequest request) {
		String newText = request.getParameter(getParamName());
		if (!ObjectUtils.nullSafeEquals(newText, getText())) {
			Object oldValue = getValue();
			setText(newText);
			Object newValue = getValue();
			fireChangeEvent(newValue, oldValue);
		}
		validate(true);
	}

	public void renderInternal(PrintWriter writer) {
		TagWriter input = new TagWriter(writer);
		input.startEmpty(Html.INPUT)
				.attribute(Html.INPUT_TYPE, getType())
				.attribute(Html.COMMON_CLASS, getStyleClass())
				.attribute(Html.COMMON_ID, getId())
				.attribute(Html.INPUT_NAME, getParamName())
				.attribute(Html.INPUT_VALUE, getText());
		if (getMaxLength() != null) {
			input.attribute(Html.INPUT_MAX_LENGTH, getMaxLength().intValue());
		}
		input.end();
	}

	public void setValidateOnChange(boolean validateOnChange) {
		this.validateOnChange = validateOnChange;
	}

	protected void validate(boolean formSubmitted) {
		if (isRequired() && !StringUtils.hasLength(getText())) {
			ErrorUtils.reject(this, "required");
		}
	}

	public void addChangeListener(ChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(listener);
	}

	protected void fireChangeEvent(Object newValue, Object oldValue) {
		if (listeners != null) {
			ChangeEvent event = new ChangeEvent(this, newValue, oldValue);
			Iterator it = listeners.iterator();
			while (it.hasNext()) {
				ChangeListener listener = (ChangeListener) it.next();
				listener.valueChanged(event);
			}
		}
	}

	public int getEventTypes() {
		if (validateOnChange || listeners != null) {
			return JavaScriptEvent.ON_CHANGE;
		}
		return JavaScriptEvent.NONE;
	}

	public void handleJavaScriptEvent(JavaScriptEvent event) {
		if (event.getType() == JavaScriptEvent.ON_CHANGE) {
			String oldValue = getText();
			setText(event.getValue());
			fireChangeEvent(getText(), oldValue);
			ErrorUtils.removeErrors(this);
			validate(false);
			//TODO It would be nicer if the listener was notified by either FormErrors or ErrorUtils
			if (getFormListener() != null) {
				getFormListener().elementValidated(this);
			}
		}
	}

}