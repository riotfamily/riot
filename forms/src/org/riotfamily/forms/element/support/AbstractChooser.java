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

import java.io.Serializable;

import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.option.IdentityReferenceAdapter;
import org.riotfamily.forms.option.ReferenceAdapter;

public class AbstractChooser extends Element {

	private transient ReferenceAdapter referenceAdapter = new IdentityReferenceAdapter(); //TODO Use ReferenceService or make this part of the Value class
	
	public class State extends Element.State {

		private Serializable reference;
		
		@Override
		public void setValue(Object value) {
			reference = referenceAdapter.createReference(value);
		}
		
		@Override
		protected void renderElement(Html html) {
			//html.innerHTML(labelRenderer.render(getObject()));
		}
		
		@Override
		public Object getValue() {
			return referenceAdapter.resolve(reference);
		}
		
	}
}
