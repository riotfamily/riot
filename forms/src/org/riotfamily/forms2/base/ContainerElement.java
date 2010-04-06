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
package org.riotfamily.forms2.base;

import java.util.Collection;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;

public class ContainerElement extends Element {

	private List<Element> childElements = Generics.newArrayList();
	
	public void add(Element element) {
		childElements.add(element);
	}
	
	@Override
	public Collection<Element> getChildElements() {
		return childElements;
	}
		
	protected class State extends ElementState {

		private List<ElementState> states = Generics.newArrayList();
		
		protected State() {
		}
		
		State(String id) {
			super(id);
		}
		
		@Override
		protected final void onInit(Value value) {
			for (Element element : childElements) {
				ElementState state = element.createState(this, value);
				states.add(state);
			}
			onInitContainer(value);
		}
		
		protected void onInitContainer(Value value) {
		}
		
		@Override
		protected void renderElement(Html html) {
			for (ElementState state : states) {
				state.render(html);
			}
		}
		
		@Override
		public void populate(Value value) {
			for (ElementState state : states) {
				state.populate(value);
			}
		}
		
	}
}
