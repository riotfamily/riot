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
package org.riotfamily.forms.element;

import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.base.UserInterface;
import org.riotfamily.forms.client.Html;

public class TextField extends Element {

	public class State extends Element.State {

		private String text;
		
		@Override
		public void setValue(Object value) {
			if (value != null) {
				text = getConversionService().convert(value, String.class);
			}
			else {
				text = null;
			}
		}
		
		protected String getText() {
			return text;
		}

		@Override
		protected void renderElement(Html html) {
			html.input("text", text).propagate("change", "update");
		}
		
		public void update(UserInterface ui, String text) {
			this.text = text;
		}

		@Override
		public Object getValue() {
			if (getTypeInfo().getType() != null) {
				return getConversionService().convert(text, getTypeInfo().getType());
			}
			return text;
		}
		
	}
}
