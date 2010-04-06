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

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;

public class BeanValue extends AbstractContainerValue {

	private Object bean;
	
	private BeanWrapperImpl beanWrapper;

	public BeanValue(TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
		beanWrapper = new BeanWrapperImpl(typeDescriptor.getType());
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) beanWrapper.getWrappedInstance();
	}
	
	public void set(Object object) {
		bean = object;
		beanWrapper.setWrappedInstance(object);
		//beanWrapper = bean != null ? PropertyAccessorFactory.forBeanPropertyAccess(bean) : null;	
	}
		
	@Override
	protected Object getNestedObject(String name) {
		return bean != null ? beanWrapper.getPropertyValue(name) : null;
	}
	
	@Override
	protected TypeDescriptor getNestedTypeDescriptor(String name) {
		return beanWrapper.getPropertyTypeDescriptor(name);
	}

	@Override
	public void setNestedObject(String name, Object object) {
		beanWrapper.setPropertyValue(name, object);
	}

}
