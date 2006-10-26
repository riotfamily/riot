package org.riotfamily.forms;

import java.util.Collection;

import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.forms.factory.FormFactory;

/**
 * Repository of form factories.
 */
public interface FormRepository {

	public Form createForm(String formId);
	
	public Class getBeanClass(String formId);
	
	public Collection getFormIds(Class beanClass);
	
	public void registerFormFactory(String id, FormFactory formFactory);

	public Class getElementClass(String type);

	public void addListener(ConfigurationEventListener listener);

}
