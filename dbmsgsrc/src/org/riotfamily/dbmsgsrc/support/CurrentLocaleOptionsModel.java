package org.riotfamily.dbmsgsrc.support;

import java.util.Collection;
import java.util.Collections;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.options.OptionsModel;

public class CurrentLocaleOptionsModel implements OptionsModel {

	public Collection<?> getOptionValues(Element element) {
		return Collections.singletonList(element.getFormContext().getLocale());
	}

}
