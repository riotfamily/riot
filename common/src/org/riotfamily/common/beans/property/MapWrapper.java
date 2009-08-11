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
public class MapWrapper extends AbstractPropertyAccessor 
		implements ObjectWrapper {

	private Map<?, ?> map;

	private Class<?> valueClass;

	private Class<?> mapClass = HashMap.class;

	public MapWrapper(Map<?, ?> map) {
		this.map = map;
	}

	public MapWrapper(Class<?> mapClass) {
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
