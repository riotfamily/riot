/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
