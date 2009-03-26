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

import org.riotfamily.core.screen.form.FormUtils;
import org.riotfamily.forms.BeanEditor;
import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertiesEditor extends CompositeElement 
		implements Editor, BeanEditor, ChangeListener {

	private FormRepository repository;
	
	private Page masterPage;
	
	private NestedForm currentForm;
	
	private Object initialValue;
	
	private LocalizedEditorBinder binder;
	
	public PagePropertiesEditor(FormRepository repository, Form form, 
			String pageType) {
		
		this.repository = repository;
		this.masterPage = getMasterPage(form);
		this.binder = new LocalizedEditorBinder(new PagePropertiesEditorBinder((Page) form.getBackingObject()));
		this.currentForm = new PropertiesForm(pageType);
		addComponent(currentForm);
	}
	
	private Page getMasterPage(Form form) {
		Page page = (Page) form.getBackingObject();
		if (page.getNode() != null) {
			Site site = page.getSite();
			if (site == null) {
				Object parent = FormUtils.loadParent(form);
				if (parent instanceof Page) {
					site = ((Page) parent).getSite();
				}
				else if (parent instanceof Site) {
					site = (Site) parent;
				}
			}
			if (site != null) {
				Site masterSite = site.getMasterSite();
				if (masterSite != null) {
					return page.getNode().getPage(masterSite);
				}
			}
		}
		return null;
	}
	
	public String getLabel() {
		return null;
	}
	
	// -----------------------------------------------------------------
	// Implementation of the Editor interface
	// -----------------------------------------------------------------
	
	public Object getValue() {
		return currentForm.getValue();
	}

	public void setValue(Object value) {
		initialValue = value;
		currentForm.setValue(value);
	}
	
	// -----------------------------------------------------------------
	// Implementation of the BeanEditor interface
	// -----------------------------------------------------------------
	
	public void setBackingObject(Object obj) {
		currentForm.setBackingObject(obj);
	}

	public Editor getEditor(String property) {
		return currentForm.getEditor(property);
	}
	
	public void bind(Editor editor, String property) {
		throw new UnsupportedOperationException();
	}

	public void setBeanClass(Class<?> beanClass) {
		throw new UnsupportedOperationException();
	}
	
	// -----------------------------------------------------------------
	// Implementation of the ChangeListener interface
	// -----------------------------------------------------------------
	
	public void valueChanged(ChangeEvent event) {
		String handlerName = (String) event.getNewValue();
		removeComponent(currentForm);
		currentForm = new PropertiesForm(handlerName); 
		addComponent(currentForm);
		currentForm.setValue(initialValue);
		getFormListener().elementChanged(this);
	}
	
	private class PropertiesForm extends NestedForm {
		
		public PropertiesForm(String pageType) {
			String id = pageType + "-page";
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
