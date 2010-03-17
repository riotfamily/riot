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
package org.riotfamily.forms2.element;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.TypedState;
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

	@Override
	protected ElementState createState(Value value) {
		return new State();
	}
	
	protected static class State extends TypedState<Binding> {

		ElementState nestedState;
				
		@Override
		protected void initInternal(Binding binding, Value value) {
			Value nestedValue = value.getNested(binding.target);
			nestedState = binding.element.createState(this, nestedValue);
		}

		@Override
		protected Html wrap(Html html, Binding element) {
			return html.div("labeled")
				.div("label").messageText(element.target, FormatUtils.propertyToTitleCase(element.target))
				.up();
		}
		
		@Override
		public void renderInternal(Html html, Binding binding) {
			nestedState.render(html, binding.element);
		}

		@Override
		public void populateInternal(Value value, Binding binding) {
			Value nestedValue = value.getNested(binding.target);
			nestedState.populate(nestedValue, binding.element);
			value.setNested(binding.target, nestedValue.get());
		}
		
	}
}
