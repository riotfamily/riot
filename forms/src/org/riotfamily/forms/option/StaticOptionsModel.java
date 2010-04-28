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
package org.riotfamily.forms.option;

import java.util.Arrays;

import org.riotfamily.forms.base.Element;

public class StaticOptionsModel implements OptionsModel {

	private Iterable<?> options;
	
	public StaticOptionsModel(Iterable<?> options) {
		this.options = options;
	}
	
	public StaticOptionsModel(Object... options) {
		this(Arrays.asList(options));
	}

	public Iterable<?> getOptions(Element.State state) {
		return options;
	}

}
