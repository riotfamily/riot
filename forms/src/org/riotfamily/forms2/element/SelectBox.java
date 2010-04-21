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

import java.util.List;

import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.element.support.Option;
import org.riotfamily.forms2.element.support.SingleSelectElement;

public class SelectBox extends SingleSelectElement {

	@Override
	protected void buildOptionsDom(List<Option> options, Html html) {
		Html select = html.elem("select").propagate("click", "select");
		for (Option option : options) {
			select.elem("option").attr("value", option.getValue())
				.attr("selected", option.isSelected())
				.text(option.getLabel());
		}
	}

}
