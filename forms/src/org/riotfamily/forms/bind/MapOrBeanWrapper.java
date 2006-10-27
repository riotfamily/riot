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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.bind;

import java.util.Map;

import org.riotfamily.common.beans.MapWrapper;
import org.riotfamily.common.beans.ObjectWrapper;
import org.riotfamily.common.beans.ProtectedBeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;

public class MapOrBeanWrapper implements ObjectWrapper {

	private ObjectWrapper wrapper;
	
	public MapOrBeanWrapper(Class objectClass) {
		if (Map.class.isAssignableFrom(objectClass)) {
			wrapper = new MapWrapper(objectClass); 	
		}
		else {
			wrapper = new ProtectedBeanWrapper(objectClass);
		}
	}

	public Class getPropertyType(String propertyName) throws BeansException {
		return this.wrapper.getPropertyType(propertyName);
	}

	public Object getPropertyValue(String propertyName) throws BeansException {
		return this.wrapper.getPropertyValue(propertyName);
	}

	public Class getWrappedClass() {
		return this.wrapper.getWrappedClass();
	}

	public Object getWrappedInstance() {
		return this.wrapper.getWrappedInstance();
	}

	public boolean isReadableProperty(String propertyName) throws BeansException {
		return this.wrapper.isReadableProperty(propertyName);
	}

	public boolean isWritableProperty(String propertyName) throws BeansException {
		return this.wrapper.isWritableProperty(propertyName);
	}

	public void setPropertyValue(PropertyValue pv) throws BeansException {
		this.wrapper.setPropertyValue(pv);
	}

	public void setPropertyValue(String propertyName, Object value) throws BeansException {
		this.wrapper.setPropertyValue(propertyName, value);
	}

	public void setPropertyValues(Map map) throws BeansException {
		this.wrapper.setPropertyValues(map);
	}

	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
		this.wrapper.setPropertyValues(pvs, ignoreUnknown, ignoreInvalid);
	}

	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
		this.wrapper.setPropertyValues(pvs, ignoreUnknown);
	}

	public void setPropertyValues(PropertyValues pvs) throws BeansException {
		this.wrapper.setPropertyValues(pvs);
	}

	public void setWrappedInstance(Object object) {
		this.wrapper.setWrappedInstance(object);
	}
		
}
