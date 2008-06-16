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

import org.riotfamily.components.model.wrapper.ValueWrapper;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.EditorBinding;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.request.FormRequest;

/**
 * Abstract base class for elements that can overwrite default values
 * inherited from a master locale.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public abstract class AbstractLocalizedElement extends TemplateElement {

	private ElementFactory elementFactory;
	
	private LocalizedEditorBinder binder;
	
	private Editor editor;
	
	private Editor display;
	
	private boolean initialized;
	
	private boolean overwrite;

	private ToggleButton toggleButton;
	
	private ValueWrapper<?> masterValue; 
	
	public AbstractLocalizedElement(ElementFactory elementFactory, 
			LocalizedEditorBinder binder) {
		
		this.elementFactory = elementFactory;
		this.binder = binder;
		setTemplate(TemplateUtils.getTemplatePath(AbstractLocalizedElement.class));
		toggleButton = new ToggleButton();
		addComponent("toggleButton", toggleButton);
	}
	
	protected abstract boolean isLocalized();
	
	protected abstract ValueWrapper<?> getMasterValue(String property);
	
	protected void initCompositeElement() {
		editor = (Editor) elementFactory.createElement(this, getForm(), true);
		addComponent("editor", editor);
		EditorBinding binding = editor.getEditorBinding();
		binder.registerElement(binding, this);
		if (isLocalized()) {
			display = (Editor) elementFactory.createElement(this, getForm(), false);
			display.setEnabled(false);
			display.setEditorBinding(binding);
			addComponent("display", display);
			masterValue = getMasterValue(binding.getProperty());
			if (masterValue != null) {
				display.setValue(masterValue.getValue());
			}
		}
	}
	
	public String getLabel() {
		return null;
	}
	
	public boolean isRequired() {
		return editor.isRequired();
	}
	
	public String getStyleClass() {
		return editor.getEditorBinding().getProperty();
	}
	
	public void processRequest(FormRequest request) {
		if (overwrite) {
			super.processRequest(request);
		}
	}
	
	protected void renderTemplate(PrintWriter writer) {
		if (!initialized) {
			overwrite = display == null || editor.getEditorBinding().getValue() != null;
			if (!overwrite && masterValue != null) {
				editor.setValue(masterValue.deepCopy().getValue());
			}
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
