package org.riotfamily.forms.factory;

import org.riotfamily.forms.Form;

/**
 * Factory interface that can be registered with a 
 * {@link org.riotfamily.forms.FormRepository} to create new form instances.
 */
public interface FormFactory extends ContainerElementFactory {

	public Class getBeanClass();

	public Form createForm();
	
}
