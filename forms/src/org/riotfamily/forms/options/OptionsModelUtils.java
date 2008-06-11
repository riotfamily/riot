package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.OptionsModelFactory;

public class OptionsModelUtils {

	public static OptionsModel createOptionsModel(Object model, Element element) {
		if (model instanceof OptionsModel) {
			return (OptionsModel) model;
		}
		for (OptionsModelFactory factory : element.getFormContext().getOptionValuesAdapters()) {
			if (factory.supports(model)) {
				return factory.createOptionsModel(model, element);
			}
		}
		throw new IllegalStateException("No adapter registered for " + model);
	}
	
	public static Collection<?> getOptionValues(Object model, Element element) {
		return createOptionsModel(model, element).getOptionValues(element);
	}
}
