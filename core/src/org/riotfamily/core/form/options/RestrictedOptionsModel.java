package org.riotfamily.core.form.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.forms.options.OptionsModelUtils;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class RestrictedOptionsModel implements OptionsModel {
	
	private Object model;
	
	public RestrictedOptionsModel(Object model) {
		this.model = model;
	}

	public Collection<?> getOptionValues(Element element) {
		Collection<?> sourceOptions = OptionsModelUtils.adapt(model, element).getOptionValues(element);
		ArrayList<Object> result = Generics.newArrayList();
		Iterator<?> it = sourceOptions.iterator();
		while (it.hasNext()) {
			Object option = it.next();
			if (AccessController.isGranted("use-option", option)) {
				result.add(option);
			}
		}
		return result;
	}

	

}
