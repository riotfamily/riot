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

import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.TypedState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;

public class TextField extends Element {

	@Override
	protected ElementState createState(Value value) {
		return new State(getText(value));
	}

	private String getText(Value value) {
		//return getForm().getConversionService().convert(value.get(), String.class);
		Object obj = value.get();
		return obj != null ? obj.toString() : null;
	}
	
	private Object getObject(String text) {
		return text;
	}

	protected static class State extends TypedState<TextField> {

		private String text;
		
		public State(String text) {
			this.text = text;
		}

		@Override
		protected void renderInternal(Html html, TextField element) {
			html.input("text", text).propagate("change", "update");
		}
		
		public void update(UserInterface ui, TextField element, String text) {
			this.text = text;
		}

		@Override
		protected void populateInternal(Value value, TextField element) {
			value.set(element.getObject(text));
		}
		
	}
}
