package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.OptionsModelAdapter;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CollectionOptionsModelAdapter implements OptionsModelAdapter {

	public boolean supports(Object model) {
		return model instanceof Collection<?>;
	}
	
	public OptionsModel adapt(Object model, Element element) {
		return new StaticOptionsModel((Collection<?>) model);
	}
}
