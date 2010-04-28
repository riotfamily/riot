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

import org.riotfamily.forms.base.ContainerElement;
import org.riotfamily.forms.value.TypeHint;
import org.riotfamily.forms.value.Value;

public class NestedForm extends ContainerElement implements TypeHint {

	private Class<?> type;
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public class State extends ContainerElement.State {
				
		@Override
		public void populate(Value value) {
			getOrCreate(value, null, null);
			super.populate(value);
		}

	}
}
