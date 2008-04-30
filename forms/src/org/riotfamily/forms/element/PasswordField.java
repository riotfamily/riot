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

import org.riotfamily.common.util.HashUtils;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;

public class PasswordField extends TemplateElement 
		implements Editor, DHTMLElement, ResourceElement {

	private boolean togglePlaintext;
	
	private String hash;
	
	private boolean strengthMeter;
	
	private TextField textField;
	
	private String initialValue;
	
	private boolean showInput = true;
	
	public PasswordField() {
		textField = new TextField("password");
		textField.setConfirmMessageKey("label.passwordField.confirmInput");
		addComponent("input", textField);
		addComponent("toggleButton", new ToggleButton());
	}
	
	public boolean isCompositeElement() {
		return false;
	}

	public void setConfirm(boolean confirm) {
		textField.setConfirm(confirm);
	}
	
	public void setRequired(boolean required) {
		super.setRequired(required);
		textField.setRequired(required);
	}
	
	public void setTogglePlaintext(boolean togglePlaintext) {
		this.togglePlaintext = togglePlaintext;
	}

	public boolean isTogglePlaintext() {
		return togglePlaintext;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public boolean isStrengthMeter() {
		return strengthMeter;
	}

	public void setStrengthMeter(boolean strengthMeter) {
		this.strengthMeter = strengthMeter;
	}

	public String getInitScript() {
		return showInput && (togglePlaintext || strengthMeter) 
				? TemplateUtils.getInitScript(this) : null;
	}
	
	public FormResource getResource() {
		if (strengthMeter) {
			return new ScriptResource("riot-js/pw-strength.js", "PasswordStrengthMeter");
		}
		return null;
	}

	public Object getValue() {
		if (!showInput) {
			return initialValue;
		}
		String value = (String) textField.getValue();
		if (hash != null && !hash.equalsIgnoreCase("plain") 
				&& !hash.equalsIgnoreCase("false")) {
			
			value = HashUtils.hash(value, hash);
		}
		return value;
	}

	public void setValue(Object value) {
		initialValue = (String) value;
		if (isPasswordSet() && showInput) {
			toggle();
		}
	}
	
	public boolean isPasswordSet() {
		return initialValue != null;
	}
	
	public boolean isShowInput() {
		return showInput;
	}
	
	protected void toggle() {
		showInput = !showInput;
		textField.setEnabled(showInput);
		if (getFormListener() != null) {
			getFormListener().elementChanged(this);			
		}
	}
	
	private class ToggleButton extends Button {
		
		public String getLabelKey() {
			return showInput 
					? "label.passwordField.keep" 
					: "label.passwordField.change";
		}

		protected void onClick() {
			toggle();
		}
		
		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}
	
}
