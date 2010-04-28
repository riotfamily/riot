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

import java.util.Collection;
import java.util.Collections;

import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.value.TypeHint;
import org.riotfamily.forms.value.TypeInfo;
import org.riotfamily.forms.value.Value;

public abstract class ElementWrapper extends Element {

	private Element wrappedElement;
	
	protected ElementWrapper() {
	}
	
	protected ElementWrapper(Element wrappedElement) {
		wrap(wrappedElement);
	}
	
	protected void wrap(Element element) {
		this.wrappedElement = element;
	}
	
	@Override
	public Collection<Element> getChildElements() {
		return Collections.singleton(wrappedElement);
	}

	protected class State extends Element.State {

		private Element.State wrappedState;
		
		@Override
		protected final void onInit() {
			onInitWrapper();
			wrappedState = wrappedElement.createState(this);
		}
		
		protected void onInitWrapper() {
		}

		@Override
		public TypeInfo getTypeInfo() {
			if (wrappedElement instanceof TypeHint) {
				Class<?> type = ((TypeHint) wrappedElement).getType();
				if (type != null) {
					return new TypeInfo(type);
				}
			}
			return super.getTypeInfo();
		}
		
		@Override
		public void setValue(Object value) {
			wrappedState.setValue(value);
		}
		
		@Override
		public Object getValue() {
			return wrappedState.getValue();
		}
		
		@Override
		public void populate(Value value) {
			wrappedState.populate(value);
		}
		
		@Override
		protected void renderElement(Html html) {
			wrappedState.render(html);
		}
			
	}
}
