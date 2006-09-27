package org.riotfamily.forms.factory.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.factory.FormDefinitionException;
import org.riotfamily.forms.factory.FormFactory;


/**
 *
 */
public abstract class AbstractFormRepository implements FormRepository {
	
	private HashMap factories = new HashMap();

	private HashMap customElements;
	
			
	public void setCustomElements(Properties props) 
			throws ClassNotFoundException {
		
		customElements = new HashMap();
		Iterator it = props.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry prop = (Map.Entry) it.next();
			String className = (String) prop.getValue();
			if (className != null) {
				Class elementClass = Class.forName(className);
				customElements.put(prop.getKey(), elementClass);
			}
		}
	}

	public Class getElementClass(String type) {
		if (customElements == null) {
			return null;
		}
		return (Class) customElements.get(type);
	}
	
	protected FormFactory getFormFactory(String id) {
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
	
	public Class getBeanClass(String id) {
		return getFormFactory(id).getBeanClass();
	}
	
	public Collection getFormIds(Class beanClass) {
		ArrayList ids = new ArrayList();
		Iterator it = factories.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			FormFactory factory = (FormFactory) entry.getValue();
			if (factory.getBeanClass().isAssignableFrom(beanClass)) {
				ids.add(entry.getKey());
			}
		}
		return ids;
	}

	public void registerFormFactory(String id, FormFactory formFactory) {
		factories.put(id, formFactory);
	}
	
	protected HashMap getFactories() {
		return this.factories;
	}
	
}
