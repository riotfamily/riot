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

import java.util.ListIterator;

public class DirtyCheckListIterator<E> extends DirtyCheckIterator<E> 
		implements ListIterator<E> {

	private ListIterator<E> it;

	public DirtyCheckListIterator(ListIterator<E> it) {
		super(it);
		this.it = it;
	}

	// -----------------------------------------------------------------------
	// Methods that modify the backing list
	// -----------------------------------------------------------------------
	
	public void add(E e) {
		dirty();
		it.add(e);
	}

	public void set(E e) {
		dirty();
		it.set(e);
	}
	
	// -----------------------------------------------------------------------
	// Methods that don't modify the backing list
	// -----------------------------------------------------------------------
	
	public boolean hasPrevious() {
		return it.hasPrevious();
	}

	public int nextIndex() {
		return it.nextIndex();
	}

	public E previous() {
		return it.previous();
	}

	public int previousIndex() {
		return it.previousIndex();
	}
	
}
