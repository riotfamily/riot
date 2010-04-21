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

import org.springframework.core.convert.TypeDescriptor;

public abstract class AbstractContainerValue extends AbstractMutableValue {

	public AbstractContainerValue(TypeDescriptor typeDescriptor) {
		super(typeDescriptor);
	}
	
	@Override
	public final Value getNested(String name) {
		Object obj = getNestedObject(name);
		TypeDescriptor td = getNestedTypeDescriptor(name);
		boolean defer = td.getType() == null || td.getType().equals(Object.class);
		Value nestedValue = ValueFactory.createValue(td, defer);
		nestedValue.set(obj);
		return nestedValue;
	}
	
	@Override
	public final void setNested(String name, Object object) {
		setNestedObject(name, object);
	}
	
	protected Object getNestedObject(String name) {
		throw new IllegalStateException("The value does not support nested access");
	}
	
	protected TypeDescriptor getNestedTypeDescriptor(String name) {
		throw new IllegalStateException("The value does not support nested access");
	}
	
	protected void setNestedObject(String name, Object object) {
		throw new IllegalStateException("The value does not support nested access");
	}
	
}
