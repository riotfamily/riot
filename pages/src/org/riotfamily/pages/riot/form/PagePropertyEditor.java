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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.form;

import java.beans.PropertyEditor;
import java.io.PrintWriter;

import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.EditorBinding;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.request.FormRequest;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertyEditor extends CompositeElement implements Editor {

	private Editor editor;
	
	private Element display;
	
	private boolean overwrite;

	private ToggleButton toggleButton;
	
	public PagePropertyEditor(Editor editor, Element display) {
		this.editor = editor;
		this.display = display;
		toggleButton = new ToggleButton();
		addComponent(editor);
		addComponent(display);
		addComponent(toggleButton);
		setSurroundBySpan(true);
		editor.getEditorBinding().setEditor(this);
	}

	public ToggleButton getToggleButton() {
		return this.toggleButton;
	}
		
	public String getLabel() {
		return editor.getLabel();
	}
	
	public void setValue(Object value) {
		editor.setValue(value);
		overwrite = value != null;
	}
	
	public Object getValue() {
		return overwrite ? editor.getValue() : null;
	}

	protected void processRequestCompontents(FormRequest request) {
		editor.processRequest(request);
		toggleButton.processRequest(request);
	}
	
	protected void renderComponents(PrintWriter writer) {
		if (overwrite) {
			editor.render(writer);
		}
		else {
			display.render(writer);
		}
	}
	
	protected void toggle() {
		overwrite = !overwrite;
		if (getFormListener() != null) {
			getFormListener().elementChanged(this);			
		}
	}

	private class ToggleButton extends Button {
				
		public String getCssClass() {
			return overwrite 
					? "button button-inherit" 
					: "button button-overwrite";
		}
		
		protected void onClick() {
			toggle();
			if (getFormListener() != null) {
				getFormListener().elementChanged(this);			
			}
		}
		
		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}
	
}
