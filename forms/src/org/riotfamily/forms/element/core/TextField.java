package org.riotfamily.forms.element.core;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.element.support.AbstractTextElement;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.i18n.MessageUtils;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.ScriptSequence;
import org.springframework.util.ObjectUtils;

/**
 * A text input field.
 */
public class TextField extends AbstractTextElement implements DHTMLElement, 
		ResourceElement {

	private static final String CONFIRM_SUFFIX = "-confirm";
	
	private static final String DEFAULT_CONFIRM_MESSAGE_KEY = 
			"label.textField.confirmInput";
	
	private static final String DEFAULT_REGEX_MISMATCH_MESSAGE_KEY =
			"error.textField.regexMismatch";
	
	private static final List RESOURCES = Collections.singletonList(
			new ScriptSequence(new ScriptResource[] {
				Resources.PROTOTYPE, new ScriptResource("riot-js/text-input.js")
			}));
	
	private boolean confirm;
	
	private String confirmText = null;
	
	private String confirmMessageKey;
	
	private String confirmMessageText;
	
	private String regex;
	
	private String regexMismatchMessageKey = DEFAULT_REGEX_MISMATCH_MESSAGE_KEY;
	
	private String regexMismatchMessageText;
	
	
	public TextField() {
		this("text");
	}
	
	public TextField(String s) {		
		super(s);
	}
	
	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}	

	public void setConfirmMessageKey(String confirmMessageKey) {
		this.confirmMessageKey = confirmMessageKey;
	}

	public void setConfirmMessageText(String confirmMessageText) {
		this.confirmMessageText = confirmMessageText;
	}	

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public void setRegexMismatchMessageKey(String regexMismatchMessageKey) {
		this.regexMismatchMessageKey = regexMismatchMessageKey;
	}

	public void setRegexMismatchMessageText(String regexMismatchMessageText) {
		this.regexMismatchMessageText = regexMismatchMessageText;
	}

	public void renderInternal(PrintWriter writer) {
		if (confirm) {
			DocumentWriter doc = new DocumentWriter(writer);
			doc.start(Html.DIV).attribute(Html.COMMON_CLASS, "confirm-text");
			doc.body();
			super.renderInternal(writer);
			String msg = MessageUtils.getMessage(this, getConfirmMessage());
			doc.start(Html.P).body(msg).end();
			
			doc.startEmpty(Html.INPUT)
					.attribute(Html.INPUT_TYPE, getType())
					.attribute(Html.COMMON_CLASS, getStyleClass())
					.attribute(Html.INPUT_NAME, getConfirmParamName())
					.attribute(Html.INPUT_VALUE, 
					confirmText != null ? confirmText : getText());
			
			doc.closeAll();
		}
		else {
			super.renderInternal(writer);
		}
	}
	
	public void processRequest(HttpServletRequest request) {
		if (confirm) {
			confirmText = request.getParameter(getConfirmParamName());
		}
		super.processRequest(request);
	}

	protected void validate() {		
		super.validate();
		if (confirm) {
			if (!ObjectUtils.nullSafeEquals(getText(), confirmText)) {
				ErrorUtils.reject(this, "confirmFailed");
			}
		}
		if (regex != null) {
			Pattern pattern = Pattern.compile(regex);
			if (!pattern.matcher(getText()).matches()) {
				getForm().getErrors().rejectValue(getFieldName(), 
						regexMismatchMessageKey, regexMismatchMessageText);
			}
			
		}
	}

	protected String getConfirmParamName() {
		return getParamName() + CONFIRM_SUFFIX;
	}
	
	protected String getConfirmMessage() {
		if (confirmMessageText != null) {
			return confirmMessageText;			
		}
		else if (confirmMessageKey != null){
			return MessageUtils.getMessage(this, confirmMessageKey);
		}		
		else {
			return MessageUtils.getMessage(this, getDefaultConfirmMessageKey());
		}
	}
	
	protected String getDefaultConfirmMessageKey() {
		return DEFAULT_CONFIRM_MESSAGE_KEY;
	}
	
	public String getInitScript() {
		if (regex != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("TextInput.create('");
			sb.append(getId());
			sb.append("', '");
			sb.append(regex);
			sb.append("', '");
			sb.append(getFormContext().getMessageResolver().getMessage(
					regexMismatchMessageKey, null, regexMismatchMessageKey));
			
			sb.append("');");
			return sb.toString();
		}
		return null;
	}
	
	public String getPrecondition() {
		return regex != null ? "TextInput" : null;
	}
	
	public Collection getResources() {
		return regex != null ? RESOURCES : null;
	}
	
}
