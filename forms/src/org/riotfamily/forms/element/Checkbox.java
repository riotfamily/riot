package org.riotfamily.forms.element;

import java.io.PrintWriter;

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.AbstractEditorBase;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.request.FormRequest;


/**
 * A Checkbox widget.
 */
public class Checkbox extends AbstractEditorBase implements Editor {

	private boolean checked;

	private Object checkedValue = Boolean.TRUE;
	
	private Object uncheckedValue = Boolean.FALSE;

	private boolean checkedByDefault = false;
	
	/**
	 * Sets the value representing the element's checked state. Defaults to 
	 * <code>Boolean.TRUE</code>
	 */
	public void setCheckedValue(Object checkedValue) {
		this.checkedValue = checkedValue;
	}
	
	/**
	 * Sets the value representing the element's unchecked state. Defaults to 
	 * <code>Boolean.FALSE</code>
	 */
	public void setUncheckedValue(Object uncheckedValue) {
		this.uncheckedValue = uncheckedValue;
	}
	
	public void setCheckedByDefault(boolean checkedByDefault) {
		this.checkedByDefault = checkedByDefault;
	}
	
	public boolean isCheckedByDefault() {
		return this.checkedByDefault;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {
		return checked;
	}

	public String getEventTriggerId() {		
		return getId() + "-event-source";
	}
	
	public void renderInternal(PrintWriter writer) {
		TagWriter inputTag = new TagWriter(writer);
		inputTag.startEmpty("input")
				.attribute("type", "checkbox")
				.attribute("id", getEventTriggerId())
				.attribute("name", getParamName())				
				.attribute("class", getStyleClass())
				.attribute("checked", checked)
				.attribute("disabled", !isEnabled())
				.end();
	}
	
	@Override
	protected String getSystemStyleClass() {
		return "checkbox";
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	public void setValue(Object value) {
		if (value != null) {
			this.checked = checkedValue.equals(value);
		}
		else {
			this.checked = checkedByDefault;
		}
	}

	/**
	 * Returns the checked or unchecked value depending on the element's state.
	 * 
	 * @see org.riotfamily.forms.Editor#getValue()
	 * @see #setCheckedValue(Object)
	 * @see #setUncheckedValue(Object)
	 * @see #isChecked()
	 */
	public Object getValue() {
		return checked ? checkedValue : uncheckedValue;
	}
	
	public void processRequest(FormRequest request) {
		Object newValue = request.getParameter(getParamName());
		checked = newValue != null;
		validate();
	}
	
	protected void validate() {
		if (isRequired() && !isChecked()) {
			ErrorUtils.reject(this, "required");
		}
	}

}