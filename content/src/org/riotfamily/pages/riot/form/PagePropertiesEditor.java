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
import org.riotfamily.forms.BeanEditor;
import org.riotfamily.forms.CompositeElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.MapEditorBinder;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.config.PageType;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PagePropertiesEditor extends CompositeElement 
		implements Editor, BeanEditor, ChangeListener {

	private FormRepository repository;
	
	private Form form;
	
	private NestedForm currentForm;
	
	private Object initialValue;
	
	private EditorBinder binder;
	
	public PagePropertiesEditor(FormRepository repository, Form form, 
			PageType pageType) {
		
		this.repository = repository;
		this.form = form;
		this.binder = new MapEditorBinder(Content.class);
		this.currentForm = new PropertiesForm(pageType);
		addComponent(currentForm);
	}
		
	@Override
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
		PageType pageType = (PageType) event.getNewValue();
		removeComponent(currentForm);
		currentForm = new PropertiesForm(pageType); 
		addComponent(currentForm);
		getFormListener().elementChanged(this);
		currentForm.setValue(initialValue);
	}
	
	private class PropertiesForm extends NestedForm {
		
		public PropertiesForm(PageType pageType) {
			setRequired(true);
			setEditorBinder(binder);
			setStyleClass(pageType.getForm());
			
			addPagePropertyElements("page");
			addPagePropertyElements(pageType.getForm());
		}
		
		private void addPagePropertyElements(String id) {
			if (repository.containsForm(id)) {
				FormFactory factory = repository.getFormFactory(id);
				for (ElementFactory ef : factory.getChildFactories()) {
					addElement(ef.createElement(this, form, true));
				}
			}
		}
	}
	
}
