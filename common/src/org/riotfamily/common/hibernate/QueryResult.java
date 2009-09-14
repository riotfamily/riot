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
package org.riotfamily.common.hibernate;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.hibernate.Query;
import org.hibernate.type.Type;
import org.riotfamily.common.util.Generics;

/**
 * Implementation of the {@link List} interface that provides information
 * about the Hibernate type(s) returned by a query. This way type information
 * is available even if the query did not return any results.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class QueryResult<T> implements List<T> {

	private List<T> result;
	
	private List<Class<?>> resultClasses;

	@SuppressWarnings("unchecked")
	public QueryResult(Query query) {
		this.result = query.list();
		resultClasses = Generics.newArrayList();
		for (Type type : query.getReturnTypes()) {
			resultClasses.add(type.getReturnedClass());
		}
	}

	public QueryResult(List<T> result, List<Class<?>> classes) {
		this.result = result;
		this.resultClasses = classes;
	}
	
	public QueryResult(List<T> result, Class<?> clazz, Class<?>... additionalClasses) {
		this.result = result;
		this.resultClasses = Generics.newArrayList();
		this.resultClasses.add(clazz);
		if (additionalClasses != null) {
			for (Class<?> c : additionalClasses) {
				this.resultClasses.add(c);
			}
		}
	}
	
	public List<Class<?>> getResultClasses() {
		return resultClasses;
	}
	
	// -----------------------------------------------------------------------
	// Implementation of the List interface
	// -----------------------------------------------------------------------

	public void add(int index, T element) {
		result.add(index, element);
	}

	public boolean add(T e) {
		return result.add(e);
	}

	public boolean addAll(Collection<? extends T> c) {
		return result.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		return result.addAll(index, c);
	}

	public void clear() {
		result.clear();
	}

	public boolean contains(Object o) {
		return result.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return result.containsAll(c);
	}

	public boolean equals(Object o) {
		return result.equals(o);
	}

	public T get(int index) {
		return result.get(index);
	}

	public int hashCode() {
		return result.hashCode();
	}

	public int indexOf(Object o) {
		return result.indexOf(o);
	}

	public boolean isEmpty() {
		return result.isEmpty();
	}

	public Iterator<T> iterator() {
		return result.iterator();
	}

	public int lastIndexOf(Object o) {
		return result.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return result.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return result.listIterator(index);
	}

	public T remove(int index) {
		return result.remove(index);
	}

	public boolean remove(Object o) {
		return result.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return result.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return result.retainAll(c);
	}

	public T set(int index, T element) {
		return result.set(index, element);
	}

	public int size() {
		return result.size();
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return result.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return result.toArray();
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		return result.toArray(a);
	}
	
}
