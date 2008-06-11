package org.riotfamily.forms.options;

import java.util.Collection;

public interface DependentOptionsModel<T> {

	public String getParentProperty();
	
	public Collection<?> getOptionValues(T parent);

}
