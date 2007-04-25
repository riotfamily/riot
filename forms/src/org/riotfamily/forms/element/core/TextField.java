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
package org.riotfamily.forms.element.core;

import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.FormRequest;
import org.riotfamily.forms.element.support.AbstractTextElement;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.support.MessageUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A text input field.
 */
public class TextField extends AbstractTextElement {

	private static final String CONFIRM_SUFFIX = "-confirm";
	
	private static final String DEFAULT_CONFIRM_MESSAGE_KEY = 
			"label.textField.confirmInput";
	
	private static final String DEFAULT_REGEX_MISMATCH_MESSAGE_KEY =
			"error.textField.regexMismatch";
	
	private boolean confirm;
	
	private String confirmText = null;
	
	private String confirmMessageKey;
	
	private String confirmMessageText;
	
	private Pattern pattern;
	
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
		this.pattern = Pattern.compile(regex);
		setValidateOnChange(true);
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
	
	public void processRequest(FormRequest request) {
		if (confirm) {
			confirmText = request.getParameter(getConfirmParamName());
		}
		super.processRequest(request);
	}

	protected void validate(boolean formSubmitted) {		
		super.validate(formSubmitted);
		if (formSubmitted && confirm) {
			if (!ObjectUtils.nullSafeEquals(getText(), confirmText)) {
				ErrorUtils.reject(this, "error.textField.confirmationFailed");
			}
		}
		if (pattern != null && StringUtils.hasLength(getText())) {
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

}
