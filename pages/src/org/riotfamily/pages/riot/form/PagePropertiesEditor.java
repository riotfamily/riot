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

import java.util.Iterator;

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
	
	public PagePropertiesEditor(FormRepository repository, Page masterPage, 
			String handlerName) {
		
		this.repository = repository;
		this.masterPage = masterPage;
		this.currentForm = createNestedForm(handlerName);
		addComponent(currentForm);
		setSurroundByDiv(true);
	}

	private NestedForm createNestedForm(String handlerName) {
		String id = handlerName + "-page";
		NestedForm nestedForm = new NestedForm();
		nestedForm.setRequired(true);
		nestedForm.setIndent(false);
		nestedForm.setEditorBinder(new PagePeopertiesEditorBinder());
		nestedForm.setStyleClass(id);
		
		addPagePropertyEditors(nestedForm, "all-pages", masterPage);
		addPagePropertyEditors(nestedForm, id, masterPage);
		
		if (masterPage == null) {
			addPagePropertyEditors(nestedForm, "master-" + id, null);
		}
		nestedForm.setValue(initialValue);
		return nestedForm;
	}
	
	private boolean addPagePropertyEditors(NestedForm nestedForm, String id, Page masterPage) {
		boolean present = false;
		if (repository.containsForm(id)) {
			FormFactory factory = repository.getFormFactory(id);
			Iterator it = factory.getChildFactories().iterator();
			while (it.hasNext()) {
				ElementFactory ef = (ElementFactory) it.next();
				nestedForm.addElement(new PagePropertyEditor(ef, masterPage));
				present = true;
			}
		}
		return present;
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
		currentForm = createNestedForm(handlerName);
		addComponent(currentForm);
		getFormListener().elementChanged(this);
	}
	
}
