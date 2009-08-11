package org.riotfamily.forms.factory;

import java.util.HashMap;

import org.riotfamily.forms.Form;

public abstract class AbstractFormRepository implements FormRepository {

	private HashMap<String, FormFactory> factories = new HashMap<String, FormFactory>();

	public boolean containsForm(String id) {
		return factories.containsKey(id);
	}

	public FormFactory getFormFactory(String id) {
		FormFactory factory = (FormFactory) factories.get(id);
		if (factory == null) {
			throw new FormDefinitionException("No such form: " + id);
		}
		return factory;
	}

	public Form createForm(String id) {
		Form form = getFormFactory(id).createForm();
		form.setId(id);
		return form;
	}

	public Class<?> getBeanClass(String id) {
		return getFormFactory(id).getBeanClass();
	}

	public void registerFormFactory(String id, FormFactory formFactory) {
		factories.put(id, formFactory);
	}

	protected HashMap<String, FormFactory> getFactories() {
		return this.factories;
	}

}
