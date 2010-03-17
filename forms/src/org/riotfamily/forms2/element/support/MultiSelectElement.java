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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.value.Value;

public abstract class MultiSelectElement extends SelectElement {

	@Override
	protected Class<?> getRequiredType() {
		return Set.class;
	}
	
	@Override
	protected boolean isSelected(Object option, Object value) {
		if (value == null) {
			return option == null;
		}
		return ((Set<?>) value).contains(option);
	}
	
	@Override
	protected SelectionState createEmptyState() {
		return new State();
	}

	protected static class State extends SelectionState {

		public void select(List<String> values) {
			for (Option option : options) {
				option.setSelected(values.contains(option.getValue()));
			}
		}
		
		protected List<Serializable> getSelectedReferences() {
			List<Serializable> refs = Generics.newArrayList();
			for (Option option : options) {
				if (option.isSelected()) {
					refs.add(option.getReference());
				}
			}
			return refs;
		}
		
		@Override
		public void populateInternal(Value value, SelectElement element) {
			Set<Object> set = value.getOrCreate(LinkedHashSet.class);
			set.clear();
			for (Option option : options) {
				if (option.isSelected()) {
					set.add(element.resolve(option.getReference()));
				}
			}
		}
		
	}
}
