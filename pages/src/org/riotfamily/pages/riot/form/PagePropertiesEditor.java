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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.form;

import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Page;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertiesEditor extends CompositeElement 
		implements Editor, ChangeListener {

	private FormRepository repository;
	
	private Page masterPage;
	
	private NestedForm currentForm;
	
	private Object initialValue;
	
	private LocalizedEditorBinder binder;
	
	public PagePropertiesEditor(FormRepository repository, Page masterPage, 
			String handlerName) {
		
		this.repository = repository;
		this.masterPage = masterPage;
		this.binder = new LocalizedEditorBinder(new PagePropertiesEditorBinder());
		this.currentForm = new PropertiesForm(handlerName);
		addComponent(currentForm);
	}
	
	public String getLabel() {
		return null;
	}
	
	public Object getValue() {
		return currentForm.getValue();
	}

	public void setValue(Object value) {
		initialValue = value;
		currentForm.setValue(value);
	}

	public void valueChanged(ChangeEvent event) {
		String handlerName = (String) event.getNewValue();
		removeComponent(currentForm);
		currentForm = new PropertiesForm(handlerName); 
		addComponent(currentForm);
		currentForm.setValue(initialValue);
		getFormListener().elementChanged(this);
	}
	
	private class PropertiesForm extends NestedForm {
		
		public PropertiesForm(String handlerName) {
			String id = handlerName + "-page";
			setRequired(true);
			setIndent(false);
			setEditorBinder(binder);
			setStyleClass(id);
			
			addPagePropertyElements("all-pages");
			addPagePropertyElements(id);
			
			if (masterPage == null) {
				addPagePropertyElements("master-" + id);
			}
		}
		
		private void addPagePropertyElements(String id) {
			if (repository.containsForm(id)) {
				FormFactory factory = repository.getFormFactory(id);
				for (ElementFactory ef : factory.getChildFactories()) {
					addElement(new PagePropertyElement(ef, binder, masterPage));
				}
			}
		}
	}
	
}
