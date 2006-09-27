package org.riotfamily.forms.element.support.select;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.StringUtils;

public class StaticOptionsModel implements OptionsModel {

	private Collection optionValues;
	
	public StaticOptionsModel() {
	}

	public StaticOptionsModel(Collection options) {
		this.optionValues = options;
	}

	public void setOptionValues(Collection options) {
		this.optionValues = options;
	}
	
	public void setCommaDelimitedValues(String s) {
		optionValues = new ArrayList();
		String[] tokens = StringUtils.commaDelimitedListToStringArray(s);
		for (int i = 0; i < tokens.length; i++) {
			optionValues.add(tokens[i]);
		}
	}

	public Collection getOptionValues() {
		return optionValues;
	}
}
