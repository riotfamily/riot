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
package org.riotfamily.cachius.invalidation;

import java.util.HashSet;
import java.util.Set;


public class DeferredItemInvalidator implements ItemInvalidator {

	private ThreadLocal<Invalidations> localInvalidations = new ThreadLocal<Invalidations>();

	public void defer() {
		Invalidations invalidations = (Invalidations) localInvalidations.get();
		if (invalidations == null) {
			invalidations = new Invalidations();
			localInvalidations.set(invalidations);
		}
		else {
			invalidations.level++;
		}
	}

	public void commit() {
		Invalidations invalidations = (Invalidations) localInvalidations.get();
		assert invalidations != null && invalidations.level >= 0 : "Unbalanced call to commit()";
		if (invalidations.level-- == 0) {
			invalidations.commit();
			localInvalidations.remove();
		}
	}

	public void invalidate(ItemIndex index, String tag) {
		Invalidations invalidations = (Invalidations) localInvalidations.get();
		if (invalidations == null) {
			index.invalidate(tag);
		}
		else {
			invalidations.add(index, tag);
		}
	}

	private static class Invalidations {

		private Set<String> tags = new HashSet<String>();

		private ItemIndex index;

		private int level;

		void add(ItemIndex index, String tag) {
			this.index = index;
			tags.add(tag);
		}

		void commit() {
			for (String tag : tags) {
				index.invalidate(tag);				
			}
		}

	}

}
