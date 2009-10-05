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
import java.util.List;
import java.util.ListIterator;

public class DirtyCheckList<E> extends DirtyCheckSet<E> implements List<E> {

	private List<E> list;

	public DirtyCheckList(List<E> list) {
		super(list);
		this.list = list;
	}

	public void add(int index, E element) {
		dirty();
		list.add(index, element);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		dirty();
		return list.addAll(index, c);
	}

	public E remove(int index) {
		dirty();
		return list.remove(index);
	}

	public E set(int index, E element) {
		dirty();
		return null;
	}
	
	public E get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return new DirtyCheckListIterator<E>(list.listIterator()) {
			protected void dirty() {
				DirtyCheckList.this.dirty();
			}
		};
	}

	public ListIterator<E> listIterator(int index) {
		return new DirtyCheckListIterator<E>(list.listIterator(index)) {
			protected void dirty() {
				DirtyCheckList.this.dirty();
			}
		};
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
	
}
