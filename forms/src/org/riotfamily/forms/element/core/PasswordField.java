package org.riotfamily.forms.element.core;

import java.io.PrintWriter;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.template.TemplateUtils;

public class PasswordField extends TextField {

	private static final String DEFAULT_CONFIRM_MESSAGE_KEY = 
			"label.passwordField.confirmInput";
	
	private static final String TOGGLE_PLAINTEXT_MESSAGE_KEY = 
			"label.passwordField.togglePlaintext";
	
	private boolean togglePlaintext;
	
	public PasswordField() {
		super("password");
	}

	public void setTogglePlaintext(boolean togglePlaintext) {
		this.togglePlaintext = togglePlaintext;
	}

	protected String getDefaultConfirmMessageKey() {
		return DEFAULT_CONFIRM_MESSAGE_KEY;
	}
	
	public String getButtonId() {
		return getId() + "-toggleButton";
	}
	
	public void renderInternal(PrintWriter writer) {
		if (togglePlaintext) {
			DocumentWriter doc = new DocumentWriter(writer);
			doc.start(Html.DIV).body();
			super.renderInternal(writer);
			doc.start(Html.DIV)
					.attribute(Html.COMMON_CLASS, "toggle-plaintext")
					.body();
			
			doc.startEmpty(Html.INPUT)
				.attribute(Html.INPUT_TYPE, "checkbox")
				.attribute(Html.COMMON_ID, getButtonId())
				.end();
			
			String label = getFormContext().getMessageResolver().getMessage(
					TOGGLE_PLAINTEXT_MESSAGE_KEY);
			
			doc.start(Html.LABEL).attribute(Html.LABEL_FOR, getButtonId())
					.body(label).closeAll();
		}
		else {
			super.renderInternal(writer);
		}
	}
	
	public String getInitScript() {
		String superScript = super.getInitScript();
		if (!togglePlaintext && superScript == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		if (superScript != null) {
			sb.append(superScript);
		}
		if (togglePlaintext) {
			sb.append(TemplateUtils.getInitScript(this));
		}
		return sb.toString();
	}
	
}
