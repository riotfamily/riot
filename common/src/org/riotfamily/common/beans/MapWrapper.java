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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.util.Assert;

/**
 * PropertyAccessor implemenation that works on maps.
 */
public class MapWrapper implements ObjectWrapper {

	private Map map;
	
	private Class valueClass;
	
	private Class mapClass = HashMap.class;
	
	public MapWrapper(Map map) {
		this.map = map;
	}
	
	public MapWrapper(Class mapClass) {
		this.mapClass = mapClass;
	}
	
	public void setMapClass(Class mapClass) {
		this.mapClass = mapClass;
	}

	public void setValueClass(Class valueClass) {
		this.valueClass = valueClass;
	}

	public boolean isReadableProperty(String propertyName) {
		return true;
	}

	public boolean isWritableProperty(String propertyName) {
		return true;
	}

	public Class getPropertyType(String propertyName) {
		if (valueClass != null) {
			return valueClass;
		}
		Object value = getPropertyValue(propertyName);
		if (value != null) {
			return value.getClass();
		}
		return Object.class;
	}

	public void setWrappedInstance(Object object) {
		Assert.isInstanceOf(Map.class, object);
		map = (Map) object;
	}
	
	public Object getWrappedInstance() {
		return getMap();
	}
	
	public Class getWrappedClass() {
		return mapClass;
	}
	
	protected Map getMap() {
		if (map == null) {
			map = (Map) BeanUtils.instantiateClass(mapClass);
		}
		return map;
	}
	
	public Object getPropertyValue(String propertyName) {
		return getMap().get(propertyName);
	}

	public void setPropertyValue(String propertyName, Object value) {
		getMap().put(propertyName, value);
	}

	public void setPropertyValue(PropertyValue pv) {
		setPropertyValue(pv.getName(), pv.getValue());
	}

	public void setPropertyValues(Map map) {
		getMap().putAll(map);
	}
	
	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, 
			boolean ignoreInvalid) throws BeansException {
		
		setPropertyValues(pvs);
	}

	public void setPropertyValues(PropertyValues pvs) {
		PropertyValue[] pv = pvs.getPropertyValues();
		for (int i = 0; i < pv.length; i++) {
			setPropertyValue(pv[i]);
		}
	}

	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) {
		setPropertyValues(pvs);
	}

}
