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
package org.riotfamily.pages.riot.form;

import org.riotfamily.components.model.Content;
import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.MapEditorBinder;
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
		this.binder = new LocalizedEditorBinder(new MapEditorBinder(Content.class));
		
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
			
			addSitePropertyElements("all-sites");
			if (masterSite == null) {
				addSitePropertyElements("master-sites");
			}
		}
		
		private void addSitePropertyElements(String id) {
			if (repository.containsForm(id)) {
				FormFactory factory = repository.getFormFactory(id);
				for (ElementFactory ef : factory.getChildFactories()) {
					addElement(new SitePropertyElement(ef, binder, masterSite));
				}
			}
		}
	}
	
}
