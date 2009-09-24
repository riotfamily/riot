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
import java.util.List;
import java.util.ListIterator;

import org.riotfamily.common.util.Generics;

/**
 * Implementation of the {@link List} interface that provides information
 * about the item type at runtime.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TypedList<T> implements List<T> {

	private List<T> delegate;
	
	private Class<?> itemClass;

	public TypedList(List<T> delegate, Class<?> itemClass) {
		this.delegate = delegate;
		this.itemClass = itemClass;
	}
	
	public Class<?> getItemClass() {
		return itemClass;
	}
	
	// -----------------------------------------------------------------------
	// Implementation of the List interface
	// -----------------------------------------------------------------------

	public void add(int index, T element) {
		delegate.add(index, element);
	}

	public boolean add(T e) {
		return delegate.add(e);
	}

	public boolean addAll(Collection<? extends T> c) {
		return delegate.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		return delegate.addAll(index, c);
	}

	public void clear() {
		delegate.clear();
	}

	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}

	@Override
	public boolean equals(Object o) {
		return delegate.equals(o);
	}

	public T get(int index) {
		return delegate.get(index);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	public int indexOf(Object o) {
		return delegate.indexOf(o);
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public Iterator<T> iterator() {
		return delegate.iterator();
	}

	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return delegate.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return delegate.listIterator(index);
	}

	public T remove(int index) {
		return delegate.remove(index);
	}

	public boolean remove(Object o) {
		return delegate.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return delegate.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return delegate.retainAll(c);
	}

	public T set(int index, T element) {
		return delegate.set(index, element);
	}

	public int size() {
		return delegate.size();
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return delegate.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return delegate.toArray();
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		return delegate.toArray(a);
	}
	
	// -----------------------------------------------------------------------
	// Static methods
	// -----------------------------------------------------------------------
	
	public static <T> List<T> newInstance(Collection<?> typeInfo) {
		List<T> list = Generics.newArrayList();
		return wrapIfNeeded(list, typeInfo);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> wrapIfNeeded(List<T> list, Collection<?> typeInfo) {
		if (typeInfo instanceof TypedList<?>) {
			Class<?> itemClass = ((TypedList) typeInfo).getItemClass();
			return new TypedList<T>(list, itemClass);
		}
		return list;
	}
}
