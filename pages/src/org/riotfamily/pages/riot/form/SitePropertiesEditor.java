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

import org.riotfamily.components.riot.form.ContentEditorBinder;
import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.NestedEditor;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SitePropertiesEditor extends CompositeElement 
		implements Editor, NestedEditor {

	private FormRepository repository;
	
	private Site masterSite;
	
	private LocalizedEditorBinder binder;
	
	private PropertiesForm propertiesForm;
	
	public SitePropertiesEditor(FormRepository repository, Site masterSite) {
		this.repository = repository;
		this.masterSite = masterSite;
		this.binder = new LocalizedEditorBinder(new ContentEditorBinder());
		
		propertiesForm = new PropertiesForm();
		addComponent(propertiesForm);
	}
	
	public String getLabel() {
		return null;
	}
	
	public Object getValue() {
		return propertiesForm.getValue();
	}

	public void setValue(Object value) {
		propertiesForm.setValue(value);
	}
	
	// ------------------------------------------------------------------
	// Implementation of the NestedEditor interface 
	// ------------------------------------------------------------------
		
	public Editor getEditor(String property) {
		return propertiesForm.getEditor(property);
	}
	
	private class PropertiesForm extends NestedForm {
		
		public PropertiesForm() {
			String id = "sites-Properties";
			setRequired(true);
			setIndent(false);
			setEditorBinder(binder);
			setStyleClass(id);
			
			addPagePropertyElements("all-sites");
			if (masterSite == null) {
				addPagePropertyElements("master-sites");
			}
		}
		
		private void addPagePropertyElements(String id) {
			if (repository.containsForm(id)) {
				FormFactory factory = repository.getFormFactory(id);
				for (ElementFactory ef : factory.getChildFactories()) {
					addElement(new SitePropertyElement(ef, binder, masterSite));
				}
			}
		}
	}
	
}
