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

import java.io.PrintWriter;
import java.util.Map;

import org.riotfamily.components.model.ValueWrapper;
import org.riotfamily.components.riot.form.ValueWrapperEditor;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.pages.model.Page;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertyEditor extends TemplateElement {

	private ElementFactory elementFactory;
	
	private Page masterPage;
	
	private Editor editor;
	
	private Editor display;
	
	private boolean initialized;
	
	private boolean overwrite;

	private ToggleButton toggleButton;
	
	public PagePropertyEditor(ElementFactory elementFactory, Page masterPage) {
		this.elementFactory = elementFactory;
		this.masterPage = masterPage;
		toggleButton = new ToggleButton();
		addComponent("toggleButton", toggleButton);
	}
	
	protected void initCompositeElement() {
		editor = (Editor) elementFactory.createElement(this, getForm(), true);
		addComponent("editor", editor);
		if (masterPage != null) {
			display = (Editor) elementFactory.createElement(this, getForm(), false);
			String property = editor.getEditorBinding().getProperty();
			Map properties = masterPage.getVersionContainer().getLatestVersion().getContents();
			ValueWrapper content = (ValueWrapper) properties.get(property);
			if (content != null) {
				if (editor instanceof ValueWrapperEditor) {
					display.setValue(content);
				}
				else {
					display.setValue(content.unwrap());
				}
			}
			display.setEnabled(false);
			addComponent("display", display);
		}
	}
	
	public String getLabel() {
		return null;
	}
	
	public String getStyleClass() {
		return editor.getEditorBinding().getProperty();
	}
	
	protected void renderTemplate(PrintWriter writer) {
		if (!initialized) {
			overwrite = display == null || editor.getValue() != null;
			initialized = true;
		}
		super.renderTemplate(writer);
	}
	public boolean isOverwrite() {
		return this.overwrite;
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
					? "button-toggle button-inherit" 
					: "button-toggle button-overwrite";
		}
		
		protected void onClick() {
			toggle();
		}
		
		public int getEventTypes() {
			return JavaScriptEvent.ON_CLICK;
		}
	}
	
}
