package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.OptionsModelAdapter;

public class OptionsModelUtils {

	public static OptionsModel adapt(Object options, Element element) {
		if (options instanceof OptionsModel) {
			return (OptionsModel) options;
		}
		for (OptionsModelAdapter adapter : element.getFormContext().getOptionsModelAdapters()) {
			if (adapter.supports(options)) {
				return adapter.adapt(options, element);
			}
		}
		throw new IllegalStateException("No adapter registered for " + options);
	}
	
	public static Collection<?> getOptionValues(Object model, Element element) {
		return adapt(model, element).getOptionValues(element);
	}
}
