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

import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.riotfamily.common.util.RiotLog;


/**
 * Collection whose values are stored as {@link WeakReference weak references}.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class WeakReferenceCollection<T> extends AbstractCollection<T> {

	private RiotLog log = RiotLog.get(WeakReferenceCollection.class);
	
	private Collection<WeakReference<T>> references = new ArrayList<WeakReference<T>>();
	
	/**
	 * Returns an iterator over all non-cleared values. 
	 */
	public Iterator<T> iterator() {
		return new ReferentIterator();
	}

	/**
	 * Returns the number of references. The number may include references that
	 * have already been cleared. To get an exact count call 
	 * {@link #purge()} before.
	 */
	public int size() {
		return references.size();
	}
	
	/**
	 * Adds a {@link WeakReference} referring to the given value to the internal
	 * collection of references.
	 */
	public boolean add(T value) {
		references.add(new WeakReference<T>(value));
		return true;
	}
	
	/**
	 * Clears the collection.
	 */
	public void clear() {
		references.clear();
	}
	
	/**
	 * Removes all cleared references from the internal collection.
	 */
	public void purge() {
		Iterator<T> it = iterator();
		while (it.hasNext()) {
			it.next();
		}
	}

	private class ReferentIterator implements Iterator<T> {

		private Iterator<WeakReference<T>> it = references.iterator();
		
		private T nextValue = null;
		
		public boolean hasNext() {
			while (it.hasNext()) {
				WeakReference<T> ref = it.next();
				nextValue = ref.get();
				if (nextValue == null) {
					log.debug("Item cleared, removing reference.");
					it.remove();
				}
				else {
					return true;
				}
			}
			return false;
		}

		public T next() {
			return nextValue;
		}

		public void remove() {
			it.remove();
		}
		
	}
}
