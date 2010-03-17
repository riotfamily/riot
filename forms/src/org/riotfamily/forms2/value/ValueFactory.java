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

import java.util.Map;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;

public final class ValueFactory {

	private ValueFactory() {
	}
	
	public static Value createValue(Object object, Class<?> type) {
		Value value = createValue(TypeDescriptor.valueOf(type), true);
		if (object != null) {
			value.set(object);
		}
		return value;
	}
	
	public static Value createValue(TypeDescriptor typeDescriptor) {
		return createValue(typeDescriptor, true);
	}
	
	public static Value createValue(TypeDescriptor typeDescriptor, boolean deferUnknown) {
		Class<?> type = typeDescriptor.getType();
		if (type == null) {
			return new UnknownValue();
		}
		if (Map.class.isAssignableFrom(type)) {
			return new MapValue(typeDescriptor);
		}
		if (ClassUtils.isPrimitiveOrWrapper(type)) {
			return new SimpleValue(typeDescriptor);
		}
		return deferUnknown ? new UnknownValue() : new BeanValue(typeDescriptor);
	}

}
