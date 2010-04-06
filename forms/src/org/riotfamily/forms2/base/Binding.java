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
import java.util.Collections;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;


public class Binding extends Element {

	private String target;

	private Element element;
	
	public Binding() {
	}
			
	public Binding(String target, Element element) {
		this.target = target;
		this.element = element;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	@Override
	public Collection<Element> getChildElements() {
		return Collections.singleton(element);
	}

	public class State extends ElementState {

		ElementState nestedState;
		
		@Override
		protected Html wrap(Html html) {
			return html;
		}
		
		@Override
		protected void onInit(Value value) {
			Value nestedValue = value.getNested(target);
			nestedState = element.createState(this, nestedValue);
		}

		@Override
		public void renderElement(Html html) {
			Html div = html.div("labeled");
			div.div("label").messageText(target, FormatUtils.propertyToTitleCase(target));
			nestedState.render(div);
		}

		@Override
		public void populate(Value value) {
			Value nestedValue = value.getNested(target);
			nestedState.populate(nestedValue);
			value.setNested(target, nestedValue.get());
		}
		
	}
}
