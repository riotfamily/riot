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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.riotfamily.common.util.ExceptionUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

public abstract class AbstractMutableValue implements Value {

	private TypeDescriptor typeDescriptor;
	
	private Class<?> defaultType;
	
	public AbstractMutableValue(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	public <T, D extends T> Value require(Class<T> requiredType, Class<D> defaultType) {
		Assert.isAssignable(requiredType, typeDescriptor.getType());
		this.defaultType = defaultType != null ? defaultType : requiredType;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOrCreate() {
		if (get() == null) {
			Object object;
			Class<?> type = getTypeDescriptor().getType();
			if (canInstanciate(type)) {
				object = instanciate(type);
			}
			else {
				object = instanciate(defaultType);
			}
			set(object);
		}
		return (T) get();
	}
	
	protected Object instanciate(Class<?> type) {
		try {
			return type.newInstance();
		}
		catch (Exception e) {
			throw ExceptionUtils.wrapReflectionException(e);
		}
	}
	
	private boolean canInstanciate(Class<?> type) {
		if (type != null && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
			for (Constructor<?> c : type.getConstructors()) {
				if (c.getParameterTypes().length == 0 && Modifier.isPublic(c.getModifiers())) {
					return true;
				}
			}
		}
		return false;
	}
		
	public Value getNested(String name) {
		throw new IllegalStateException("The value does not support nested access");
	}

	public void setNested(String name, Object object) {
		throw new IllegalStateException("The value does not support nested access");
	}

}
