package org.riotfamily.forms.factory;

import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.forms.Form;

/**
 * Repository of form factories.
 */
public interface FormRepository {

	public boolean containsForm(String formId);
	
	public FormFactory getFormFactory(String id);
	
	public Form createForm(String formId);
	
	public Class<?> getBeanClass(String formId);
	
	public void addListener(ConfigurationEventListener listener);

}
