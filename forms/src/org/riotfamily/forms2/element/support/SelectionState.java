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
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.base.TypedState;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;

abstract class SelectionState extends TypedState<SelectElement> {

	protected List<Option> options = Generics.newArrayList();
	
	@Override
	protected void initInternal(SelectElement element, Value value) {
		element.createOptions(this, value.get());
	}
	
	public void addOption(Serializable reference, String label, boolean selected) {
		String value = String.valueOf(options.size());
		options.add(new Option(reference, value, label, selected));
	}
	
	@Override
	protected void renderInternal(Html html, SelectElement element) {
		element.buildOptionsDom(options, html);
	};
	
}
