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
package org.riotfamily.forms.element.support;

import java.util.List;

import org.riotfamily.forms.base.ContainerState;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.base.ElementWrapper;
import org.riotfamily.forms.base.StateEvent;
import org.riotfamily.forms.base.StateEventHandler;

public class DependentElement extends ElementWrapper {

	public DependentElement(Element wrappedElement) {
		super(wrappedElement);
	}

	public static Element.State getPrecedingElement(Element.State state) {
		while (state != null) {
			Element.State parent = state.getParent();
			if (parent instanceof ContainerState) {
				ContainerState container = (ContainerState) parent;
				List<Element.State> c = container.getChildStates();
				int i = c.indexOf(state);
				if (i == -1) {
					i = c.size();
				}
				return c.get(i - 1);
			}
			state = parent;
		}
		throw new IllegalStateException("Element must be nested within a container");
	}
	
	public class State extends ElementWrapper.State {
		
		@Override
		protected void onInitWrapper() {
			getPrecedingElement(this).addStateEventHandler(new ElementUpdater(this));
		}		
	}
	
	private static class ElementUpdater implements StateEventHandler {
		
		private Element.State state;
		
		public ElementUpdater(Element.State state) {
			this.state = state;
		}

		public void handle(StateEvent event) {
			event.stop();
			state.setValue(null);
			event.getUserInterface().refresh(state);
		}
	}
}
