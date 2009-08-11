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
