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
import org.springframework.util.Assert;

public class UnknownValue implements Value {

	private Value delegate;
	
	private Value getDelegate() {
		if (delegate == null) {
			throw new IllegalStateException("Actual type is still unkown");
		}
		return delegate;
	}
	
	public <T, D extends T> Value require(Class<T> requiredType, Class<D> defaultType) {
		if (delegate == null) {
			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(requiredType);
			Assert.notNull(typeDescriptor.getType());
			delegate = ValueFactory.createValue(typeDescriptor, false);
		}
		delegate.require(requiredType, defaultType);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOrCreate() {
		return (T) getDelegate().getOrCreate();
	}
	
	public void set(Object object) {
		if (delegate == null && object != null) {
			delegate = ValueFactory.createValue(TypeDescriptor.valueOf(object.getClass()), false);
		}
		if (delegate != null) {
			delegate.set(object);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		if (delegate == null) {
			return null;
		}
		return (T) delegate.get();
	}

	public Value getNested(String name) {
		if (delegate == null) {
			return new UnknownValue();
		}
		return delegate.getNested(name);
	}
	
	public TypeDescriptor getTypeDescriptor() {
		return getDelegate().getTypeDescriptor();
	}

	public void setNested(String name, Object object) {
		getDelegate().setNested(name, object);
	}

}
