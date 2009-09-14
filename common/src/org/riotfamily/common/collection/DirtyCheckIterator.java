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

import java.util.Iterator;

public class DirtyCheckIterator<E> implements Iterator<E> {

	private Iterator<E> it;

	private boolean dirty;

	public DirtyCheckIterator(Iterator<E> it) {
		this.it = it;
	}

	public boolean isDirty() {
		return dirty;
	}
	
	protected void dirty() {
		this.dirty = true;
	}

	// -----------------------------------------------------------------------
	// Methods that modify the backing list
	// -----------------------------------------------------------------------
	
	public void remove() {
		dirty();
		it.remove();
	}
	
	// -----------------------------------------------------------------------
	// Methods that don't modify the backing list
	// -----------------------------------------------------------------------
	
	public boolean hasNext() {
		return it.hasNext();
	}

	public E next() {
		return it.next();
	}
	
}
