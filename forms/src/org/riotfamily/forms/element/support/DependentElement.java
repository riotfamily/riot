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

import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.base.ElementWrapper;
import org.riotfamily.forms.base.StateEvent;
import org.riotfamily.forms.base.StateEventHandler;

public class DependentElement extends ElementWrapper {

	public DependentElement(Element wrappedElement) {
		super(wrappedElement);
	}

	public class State extends ElementWrapper.State {
		
		@Override
		protected void onInitWrapper() {
			getPrecedingState().addStateEventHandler(new ElementUpdater(this));
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
