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
	
	public AbstractMutableValue(TypeDescriptor typeDescriptor) {
		this.typeDescriptor = typeDescriptor;
	}

	public TypeDescriptor getTypeDescriptor() {
		return typeDescriptor;
	}

	public void require(Class<?> requiredType) {
		Assert.isAssignable(requiredType, typeDescriptor.getType());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOrCreate(Class<?> defaultType) {
		if (get() == null) {
			Class<?> type = getTypeDescriptor().getType();
			Object object = instanciate(type);
			if (object == null) {
				object = instanciate(defaultType);
			}
			set(object);
		}
		return (T) get();
	}
	
	protected Object instanciate(Class<?> type) {
		if (canInstanciate(type)) {
			try {
				return type.newInstance();
			}
			catch (Exception e) {
				throw ExceptionUtils.wrapReflectionException(e);
			}
		}
		return null;
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
