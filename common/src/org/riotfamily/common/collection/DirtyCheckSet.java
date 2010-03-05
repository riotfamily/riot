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
package org.riotfamily.common.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class DirtyCheckSet<E> implements Set<E> {

	private Collection<E> collection;

	private boolean dirty;

	public DirtyCheckSet(Collection<E> collection) {
		this.collection = collection;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	protected void dirty() {
		dirty = true;
	}
	
	// -----------------------------------------------------------------------
	// Delegate methods
	// -----------------------------------------------------------------------

	public Iterator<E> iterator() {
		return new DirtyCheckIterator<E>(collection.iterator()) {
			@Override
			protected void dirty() {
				DirtyCheckSet.this.dirty();
			}
		};
	}
	
	public boolean add(E e) {
		dirty();
		return collection.add(e);
	}

	public boolean addAll(Collection<? extends E> c) {
		dirty();
		return collection.addAll(c);
	}

	public void clear() {
		dirty();
		collection.clear();
	}
	
	public boolean remove(Object o) {
		dirty();
		return collection.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		dirty();
		return collection.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		dirty();
		return collection.retainAll(c);
	}

	public boolean contains(Object o) {
		return collection.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return collection.containsAll(c);
	}

	public boolean isEmpty() {
		return collection.isEmpty();
	}

	public int size() {
		return collection.size();
	}

	public Object[] toArray() {
		return collection.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return collection.toArray(a);
	}
	
	@Override
	public int hashCode() {
		return collection.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return collection.equals(o);
	}
	
}
