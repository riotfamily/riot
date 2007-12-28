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

import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.form.ui.FormUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertyEditor extends TemplateElement implements Editor {

	ElementFactory elementFactory;
	
	private Editor editor;
	
	private Editor display;
	
	private boolean overwrite;

	private ToggleButton toggleButton;
	
	public PagePropertyEditor(ElementFactory elementFactory) {
		this.elementFactory = elementFactory;
		toggleButton = new ToggleButton();
		addComponent("toggleButton", toggleButton);
	}
	
	protected void initCompositeElement() {
		editor = (Editor) elementFactory.createElement(this, getForm(), true);
		editor.getEditorBinding().setEditor(this);
		addComponent("editor", editor);
		
		display = (Editor) elementFactory.createElement(this, getForm(), false);
		display.setEnabled(false);
		addComponent("display", display);
		
		Page page = (Page) getForm().getBackingObject();
		PageNode node = page.getNode();
		Site site = page.getSite();
		
		if (site == null) {
			Object parent = FormUtils.loadParent(getForm());
			if (parent instanceof Page) {
				site = ((Page) parent).getSite();
			}
			else if (parent instanceof Site) {
				site = (Site) parent;
			}
		}
		if (site != null) {
			Page masterPage = null;
			Site masterSite = site.getMasterSite();
			while (masterPage == null && masterSite != null) {
				masterPage = node.getPage(masterSite);
				masterSite = masterSite.getMasterSite();
			}
			if (masterPage != null) {
				String property = editor.getEditorBinding().getProperty();
				display.setValue(masterPage.getProperty(property, true));
			}
		}
	}
	
	public String getLabel() {
		return null;
	}
	
	public void setValue(Object value) {
		editor.setValue(value);
		overwrite = value != null;
	}
	
	public Object getValue() {
		return overwrite ? editor.getValue() : null;
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
