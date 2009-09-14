/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.beans.property;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.AbstractPropertyAccessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;

/**
 * PropertyAccessor implementation that works on maps.
 */
public class MapPropertyAccessor extends AbstractPropertyAccessor {

	private Map<?, ?> map;

	private Class<?> valueClass;

	private Class<?> mapClass = HashMap.class;

	public MapPropertyAccessor(Map<?, ?> map) {
		this.map = map;
	}

	public MapPropertyAccessor(Class<?> mapClass) {
		this.mapClass = mapClass;
	}

	public void setMapClass(Class<?> mapClass) {
		this.mapClass = mapClass;
	}

	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}

	public boolean isReadableProperty(String propertyName) {
		return true;
	}

	public boolean isWritableProperty(String propertyName) {
		return true;
	}

	public Class<?> getPropertyType(String propertyName) {
		if (valueClass != null) {
			return valueClass;
		}
		Object value = getPropertyValue(propertyName);
		if (value != null) {
			return value.getClass();
		}
		return Object.class;
	}

	@SuppressWarnings("unchecked")
	public void setObject(Object object) {
		Assert.isInstanceOf(Map.class, object);
		map = (Map) object;
	}

	public Object getObject() {
		return getMap();
	}

	public Class<?> getObjectClass() {
		return mapClass;
	}

	@SuppressWarnings("unchecked")
	protected Map getMap() {
		if (map == null) {
			map = (Map) BeanUtils.instantiateClass(mapClass);
		}
		return map;
	}

	public Object getPropertyValue(String propertyName) {
		return getMap().get(propertyName);
	}

	@SuppressWarnings("unchecked")
	public void setPropertyValue(String propertyName, Object value) {
		getMap().put(propertyName, value);
	}

	public void setPropertyValue(PropertyValue pv) {
		setPropertyValue(pv.getName(), pv.getValue());
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

	@SuppressWarnings("unchecked")
	public Object convertIfNecessary(Object value, Class requiredType,
			MethodParameter methodParam) throws TypeMismatchException {
		
		return value;
	}
}
