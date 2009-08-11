package org.riotfamily.forms;

import org.riotfamily.forms.options.OptionsModel;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public interface OptionsModelAdapter {

	public boolean supports(Object model);
	
	public OptionsModel adapt(Object model, Element element);

}
