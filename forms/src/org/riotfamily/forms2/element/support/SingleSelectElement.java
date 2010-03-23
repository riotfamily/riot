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

import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.value.Value;
import org.springframework.util.ObjectUtils;

public abstract class SingleSelectElement extends SelectElement {

	@Override
	protected Class<?> getRequiredType() {
		return Object.class;
	}
	
	@Override
	protected boolean isSelected(Object option, Object value) {
		return ObjectUtils.nullSafeEquals(option, value);
	}
		
	public static class State extends SelectionState {

		public void select(UserInterface ui, SingleSelectElement element, String value) {
			select(value);
		}
		
		protected void select(String value) {
			for (Option option : options) {
				option.setSelected(option.getValue().equals(value));
			}
		}
		
		protected Serializable getSelectedReference() {
			for (Option option : options) {
				if (option.isSelected()) {
					return option.getReference();
				}
			}
			return null;
		}

		@Override
		protected void populateInternal(Value value, SelectElement element) {
			value.set(element.resolve(getSelectedReference()));
		}
		
	}

}
