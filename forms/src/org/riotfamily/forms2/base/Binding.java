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

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;


public class Binding extends Element {

	private String target;

	private Element element;
	
	public Binding(String target, Element element) {
		this.target = target;
		this.element = element;
		element.setParent(this);
	}

	public static class State<T extends Binding> extends TypedState<T> {

		ElementState nestedState;

		@Override
		protected Html wrap(Html html, Element element) {
			return html;
		}
		
		@Override
		protected void initInternal(T binding, Value value) {
			Value nestedValue = value.getNested(binding.target);
			nestedState = binding.element.createState(this, nestedValue);
		}

		@Override
		public void renderInternal(Html html, T binding) {
			Html div = html.div("labeled");
			div.div("label").messageText(binding.target, FormatUtils.propertyToTitleCase(binding.target));
			nestedState.render(div, binding.element);
		}

		@Override
		public void populateInternal(Value value, T binding) {
			Value nestedValue = value.getNested(binding.target);
			nestedState.populate(nestedValue, binding.element);
			value.setNested(binding.target, nestedValue.get());
		}
		
	}
}
