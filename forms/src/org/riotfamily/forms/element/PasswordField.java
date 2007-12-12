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

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.TemplateUtils;

public class PasswordField extends TextField implements DHTMLElement {

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
		return togglePlaintext ? TemplateUtils.getInitScript(this) : null;
	}
	
}
