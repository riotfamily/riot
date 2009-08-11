package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PropertyOptionsModel implements OptionsModel {

	private String propertyName;

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Collection<?> getOptionValues(Element element) {
		Assert.notNull(propertyName, "A propertyName must be set.");
		Form form = element.getForm();
		if (form.isNew()) {
			return null;	
		}
		Object value = PropertyUtils.getProperty(form.getBackingObject(), 
				propertyName);
		
		if (value instanceof Collection) {
			return (Collection<?>) value;
		}
		return CollectionUtils.arrayToList(value);
	}
}
