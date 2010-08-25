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
package org.riotfamily.forms.element;

import java.io.PrintWriter;

import org.riotfamily.common.util.HashUtils;
import org.riotfamily.common.util.TagWriter;
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
	
	public void setConfirm(boolean confirm) {
		textField.setConfirm(confirm);
	}
	
	@Override
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
			return new ScriptResource("riot/pw-strength.js", "PasswordStrengthMeter");
		}
		return null;
	}

	public Object getValue() {
		if (!showInput) {
			return initialValue;
		}
		String value = (String) textField.getValue();
		if (value != null && hash != null 
				&& !hash.equalsIgnoreCase("plain") 
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
	
	@Override
	protected void renderInternal(PrintWriter writer) {
		//Render a faux input field because Firefox does not respect
		//autocomplete="off" on input fields preceeding a password field.
		new TagWriter(writer)
			.startEmpty("input")
			.attribute("type", "text")
			.attribute("style", "display:none")
			.end();
		
		super.renderInternal(writer);
	}
	
	protected void toggle() {
		showInput = !showInput;
		textField.setEnabled(showInput);
		if (getFormListener() != null) {
			getFormListener().elementChanged(this);			
		}
	}
	
	private class ToggleButton extends Button {
		
		@Override
		public String getLabelKey() {
			return showInput 
					? "label.passwordField.keep" 
					: "label.passwordField.change";
		}

		@Override
		protected void onClick() {
			toggle();
		}
		
		@Override
		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}
	
}
