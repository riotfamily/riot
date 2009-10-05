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
package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.OptionsModelAdapter;

public class OptionsModelUtils {

	public static OptionsModel adapt(Object options, Element element) {
		if (options instanceof OptionsModel) {
			return (OptionsModel) options;
		}
		for (OptionsModelAdapter adapter : element.getFormContext().getOptionsModelAdapters()) {
			if (adapter.supports(options)) {
				return adapter.adapt(options, element);
			}
		}
		throw new IllegalStateException("No adapter registered for " + options);
	}
	
	public static Collection<?> getOptionValues(Object model, Element element) {
		return adapt(model, element).getOptionValues(element);
	}
}
