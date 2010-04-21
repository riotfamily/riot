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

import org.riotfamily.forms2.base.ContainerElement;
import org.riotfamily.forms2.value.Value;

public class NestedForm extends ContainerElement {

	private Class<?> type;
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public class State extends ContainerElement.State {
		
		@Override
		public void populate(Value value) {
			if (type != null) {
				value.require(type, null);
			}
			super.populate(value);
		}

	}
}
