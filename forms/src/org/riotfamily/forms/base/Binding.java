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
package org.riotfamily.forms.base;

import java.util.Map;

import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.value.TypeInfo;
import org.riotfamily.forms.value.Value;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;

/**
 * Wrapper that binds an element to a property or map value.
 */
public class Binding extends ElementWrapper {

	private String target;
	
	private String label;
	
	private boolean renderLabel = true;
	
	public Binding() {
	}
			
	public Binding(String target, Element element) {
		super(element);
		this.target = target;
	}
	
	public Binding omitLabel() {
		renderLabel = false;
		return this;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public void setElement(Element element) {
		wrap(element);
	}

	@Override
	public String toString() {
		return String.format("Binding[target=%s]", target);
	}
	
	public class State extends ElementWrapper.State {
		
		TypeInfo typeInfo;
		
		@Override
		protected void onInitWrapper() {
			TypeInfo parentType = getParent().getTypeInfo();
			if (parentType.isMap()) {
				typeInfo = new TypeInfo(parentType.getMapValueType());
			}
			else {
				Assert.notNull(parentType.getType(), this.toString());
				Class<?> propertyType = new BeanWrapperImpl(parentType.getType()).getPropertyType(target);
				typeInfo = new TypeInfo(propertyType);
			}
		}
		
		@Override
		public TypeInfo getTypeInfo() {
			return typeInfo;
		}
		
		@Override
		public void setValue(Object value) {
			super.setValue(getNestedObject(value));
		}

		@Override
		public void renderElement(Html html) {
			if (renderLabel) {
				html = html.div("labeled");
				if (label == null) {
					label = String.format("{%s.%s}", getParent().getTypeInfo().getType().getName(), target);
				}
				html.div("label").messageText(label);
			}
			super.renderElement(html);
		}

		@Override
		public void populate(Value value) {
			Object parent = value.get();
			Value nestedValue = new Value(getNestedObject(parent));
			super.populate(nestedValue);
			setNestedObject(parent, nestedValue.get());
		}

		@SuppressWarnings("unchecked")
		private Object getNestedObject(Object value) {
			if (value == null) {
				return null;
			}
			if (value instanceof Map) {
				return ((Map) value).get(target);
			}
			return new BeanWrapperImpl(value).getPropertyValue(target);
		}
		
		@SuppressWarnings("unchecked")
		private void setNestedObject(Object parent, Object object) {
			if (parent instanceof Map) {
				((Map) parent).put(target, object);
			}
			else {
				new BeanWrapperImpl(parent).setPropertyValue(target, object);
			}
		}
		
		@Override
		public String toString() {
			return String.format("%s$State[id=%s]", Binding.this, id());
		}
	}
	
}
