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
package org.riotfamily.forms2.element.support;

import java.io.Serializable;

import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.option.IdentityReferenceAdapter;
import org.riotfamily.forms2.option.OptionReferenceAdapter;
import org.riotfamily.forms2.value.Value;

public class AbstractChooser {

	private transient OptionReferenceAdapter referenceAdapter = new IdentityReferenceAdapter(); //TODO
	
	protected class State extends ElementState {

		private Serializable reference;
		
		@Override
		protected void renderElement(Html html) {
			//html.innerHTML(labelRenderer.render(getObject()));
		}
		
		@Override
		public void populate(Value value) {
			value.set(getObject());
		}

		private Object getObject() {
			return referenceAdapter.resolve(reference);
		}
		
	}
}
