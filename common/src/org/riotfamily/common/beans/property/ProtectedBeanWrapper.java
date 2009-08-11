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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

/**
 * BeanWrapper that that provides access to non-public setters and getters.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ProtectedBeanWrapper extends BeanWrapperImpl
		implements ObjectWrapper {

	private Class<?> objectClass;

	public ProtectedBeanWrapper() {
		super();
	}

	public ProtectedBeanWrapper(Class<?> clazz) {
		this.objectClass = clazz;
		if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
			setObject(BeanUtils.instantiateClass(clazz));
		}
	}

	public ProtectedBeanWrapper(Object object) {
		super(object);
	}

	public void setObject(Object object) {
		super.setWrappedInstance(object);
		this.objectClass = object.getClass();
	}

	public Object getObject() {
		return getWrappedInstance();
	}

	public Class<?> getObjectClass() {
		return this.objectClass;
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
