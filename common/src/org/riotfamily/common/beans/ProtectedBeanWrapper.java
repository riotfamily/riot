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
package org.riotfamily.common.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;

public class ProtectedBeanWrapper extends BeanWrapperImpl 
		implements ObjectWrapper {

	public ProtectedBeanWrapper() {
		super();
	}

	public ProtectedBeanWrapper(Class clazz) {
		super(clazz);
	}

	public ProtectedBeanWrapper(Object object) {
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
