package org.riotfamily.forms.options;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.OptionsModelAdapter;
import org.springframework.util.CollectionUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ArrayOptionsModelAdapter implements OptionsModelAdapter {

	public boolean supports(Object model) {
		return model.getClass().isArray();
	}
	
	public OptionsModel adapt(Object model, Element element) {
		return new StaticOptionsModel(CollectionUtils.arrayToList(model));
	}
}
