package org.riotfamily.common.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;

public class ProtectedPropertyAccessor extends BeanWrapperImpl {

	public ProtectedPropertyAccessor() {
		super();
	}

	public ProtectedPropertyAccessor(Class clazz) {
		super(clazz);
	}

	public ProtectedPropertyAccessor(Object object) {
		super(object);
	}

	protected PropertyDescriptor getPropertyDescriptorInternal(
			String propertyName) throws BeansException {

		try {
			PropertyDescriptor pd = super.getPropertyDescriptorInternal(propertyName);
			if (pd == null) {
				pd = new PropertyDescriptor(propertyName, 
						findReadMethod(propertyName),
						findWriteMethod(propertyName));
			}
			else {
				if (pd.getReadMethod() == null) {
					pd.setReadMethod(findReadMethod(propertyName));
				}
				if (pd.getWriteMethod() == null) {
					pd.setWriteMethod(findWriteMethod(propertyName));
				}
			}
			return pd;
		}
		catch (IntrospectionException e) {
			return null;
		}
	}
	
	protected Method findReadMethod(String propertyName) {
		String methodName = "get" + StringUtils.capitalize(propertyName);
		Method readMethod = BeanUtils.findDeclaredMethod(
				getWrappedClass(), methodName, null);
		
		if (readMethod != null) {
			readMethod.setAccessible(true);
			return readMethod;
		}
		return null;
	}
	
	protected Method findWriteMethod(String propertyName) {
		String methodName = "set" + StringUtils.capitalize(propertyName);
		Method writeMethod = BeanUtils.findDeclaredMethodWithMinimalParameters(
				getWrappedClass(), methodName); 
			
		if (writeMethod != null) {
			writeMethod.setAccessible(true);
			return writeMethod;
		}
		return null;
	}
}
