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

import org.riotfamily.forms2.client.Html;

public class TextArea extends TextField {

	public static class State<T extends TextArea> extends TextField.State<T> {

		@Override
		protected void renderInternal(Html html, T element) {
			html.elem("textarea")
				.propagate("change", "update")
				.text(getText());
		}
		
	}
}
