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
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.value.Value;

public class ContainerElement extends Element {

	private List<Element> childElements = Generics.newArrayList();
	
	public void add(Element element) {
		childElements.add(element);
	}
	
	@Override
	public Collection<Element> getChildElements() {
		return childElements;
	}
		
	protected class State extends Element.State implements ContainerState {

		private List<Element.State> childStates = Generics.newArrayList();
		
		protected State() {
		}
		
		State(String id) {
			super(id);
		}
		
		public List<Element.State> getChildStates() {
			return Collections.unmodifiableList(childStates);
		}
		
		@Override
		protected final void onInit() {
			for (Element element : childElements) {
				Element.State state = element.createState(this);
				childStates.add(state);
			}
			onInitContainer();
		}
		
		protected void onInitContainer() {
		}
		
		@Override
		public void setValue(Object value) {
			for (Element.State state : childStates) {
				state.setValue(value);
			}
		}
		
		@Override
		protected void renderElement(Html html) {
			for (Element.State state : childStates) {
				state.render(html);
			}
		}
		
		@Override
		public void populate(Value value) {
			for (Element.State state : childStates) {
				state.populate(value);
			}
		}
		
	}
}
