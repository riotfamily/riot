package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.forms.Element;

public class StaticOptionsModel implements OptionsModel {

	private Collection<?> values;
	
	public StaticOptionsModel(Collection<?> values) {
		this.values = values;
	}

	public Collection<?> getOptionValues(Element element) {
		return values;
	}
}
