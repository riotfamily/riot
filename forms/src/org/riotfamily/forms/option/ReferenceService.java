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
import java.util.List;

import org.springframework.util.Assert;

public class ReferenceService {

	private List<ReferenceAdapter> adapters;
	
	public ReferenceService() {
	}
	
	public ReferenceService(ReferenceAdapter... adapters) {
		this.adapters = Arrays.asList(adapters);
	}
	
	public Reference createReference(Object object) {
		if (object == null) {
			return null;
		}
		int i = 0;
		for (ReferenceAdapter adapter : adapters) {
			if (adapter.supports(object)) {
				return new Reference(i, adapter.createReference(object));
			}
			i++;
		}
		throw new IllegalArgumentException("No adapter found for " + object);
	}
	
	public Object resolve(Reference reference) {
		if (reference == null) {
			return null;
		}
		ReferenceAdapter adapter = adapters.get(reference.getAdapterIndex());
		Assert.notNull(adapter, "No adapter with index " + reference.getAdapterIndex());
		return adapter.resolve(reference.getData());
	}

}
