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
package org.riotfamily.forms2.value;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.TypeDescriptor;

public class BeanValue extends AbstractContainerValue {

	private Object bean;
	
	private BeanWrapper beanWrapper;

	public BeanValue(TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) bean;
	}
	
	public void set(Object object) {
		bean = object;
		beanWrapper = bean != null ? PropertyAccessorFactory.forBeanPropertyAccess(bean) : null;	
	}
	
	@Override
	protected Object getNestedObject(String name) {
		return bean != null ? beanWrapper.getPropertyValue(name) : null;
	}
	
	@Override
	public <T, D extends T> Value require(Class<T> requiredType, Class<D> defaultType) {
		return super.require(requiredType, defaultType);
	}
	
	@Override
	protected TypeDescriptor getNestedTypeDescriptor(String name) {
		getOrCreate();
		return beanWrapper.getPropertyTypeDescriptor(name);
	}

	@Override
	public void setNestedObject(String name, Object object) {
		getOrCreate();
		beanWrapper.setPropertyValue(name, object);
	}

}
