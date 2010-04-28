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

import java.io.Serializable;
import java.util.Map;

import org.springframework.core.convert.TypeDescriptor;

public class TypeInfo implements Serializable {

	private Class<?> type;
	
	private Class<?> mapValueType;
	
	private Class<?> elementType;

	public TypeInfo(Class<?> type) {
		this.type = type;
	}
			
	public TypeInfo(TypeDescriptor typeDescriptor) {
		type = typeDescriptor.getType();
		mapValueType = typeDescriptor.getMapValueType();
		elementType = typeDescriptor.getElementType();
	}
	
	public Class<?> getType() {
		return type;
	}

	public Class<?> getMapValueType() {
		return mapValueType;
	}

	public Class<?> getElementType() {
		return elementType;
	}

	public boolean isMap() {
		return type != null && Map.class.isAssignableFrom(type);
	}
	
}
