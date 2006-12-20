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

import org.riotfamily.common.util.PropertyUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

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

	protected PropertyDescriptor getPropertyDescriptorInternal(String name) 
			throws BeansException {

		try {
			PropertyDescriptor pd = super.getPropertyDescriptorInternal(name);
			if (pd == null) {
				pd = new PropertyDescriptor(name, 
						PropertyUtils.findReadMethod(getWrappedClass(), name),
						PropertyUtils.findWriteMethod(getWrappedClass(), name));
			}
			else {
				if (pd.getReadMethod() == null) {
					pd.setReadMethod(PropertyUtils.findReadMethod(
							getWrappedClass(), name));
				}
				if (pd.getWriteMethod() == null) {
					pd.setWriteMethod(PropertyUtils.findWriteMethod(
							getWrappedClass(), name));
				}
			}
			return pd;
		}
		catch (IntrospectionException e) {
			return null;
		}
	}
	
}
