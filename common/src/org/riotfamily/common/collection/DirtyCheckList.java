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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

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

	/**
	 * Replaces each element of this list with the result of applying the
	 * operator to that element.  Errors or runtime exceptions thrown by
	 * the operator are relayed to the caller.
	 *
	 * @param operator the operator to apply to each element
	 * @throws UnsupportedOperationException if this list is unmodifiable.
	 *                                       Implementations may throw this exception if an element
	 *                                       cannot be replaced or if, in general, modification is not
	 *                                       supported
	 * @throws NullPointerException          if the specified operator is null or
	 *                                       if the operator result is a null value and this list does
	 *                                       not permit null elements
	 *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @implSpec The default implementation is equivalent to, for this {@code list}:
	 * <pre>{@code
	 *     final ListIterator<E> li = list.listIterator();
	 *     while (li.hasNext()) {
	 *         li.set(operator.apply(li.next()));
	 *     }
	 * }</pre>
	 * <p>
	 * If the list's list-iterator does not support the {@code set} operation
	 * then an {@code UnsupportedOperationException} will be thrown when
	 * replacing the first element.
	 * @since 1.8
	 */
	@Override
	public void replaceAll(UnaryOperator<E> operator) {

	}

	/**
	 * Sorts this list according to the order induced by the specified
	 * {@link Comparator}.
	 *
	 * <p>All elements in this list must be <i>mutually comparable</i> using the
	 * specified comparator (that is, {@code c.compare(e1, e2)} must not throw
	 * a {@code ClassCastException} for any elements {@code e1} and {@code e2}
	 * in the list).
	 *
	 * <p>If the specified comparator is {@code null} then all elements in this
	 * list must implement the {@link Comparable} interface and the elements'
	 * {@linkplain Comparable natural ordering} should be used.
	 *
	 * <p>This list must be modifiable, but need not be resizable.
	 *
	 * @param c the {@code Comparator} used to compare list elements.
	 *          A {@code null} value indicates that the elements'
	 *          {@linkplain Comparable natural ordering} should be used
	 * @throws ClassCastException            if the list contains elements that are not
	 *                                       <i>mutually comparable</i> using the specified comparator
	 * @throws UnsupportedOperationException if the list's list-iterator does
	 *                                       not support the {@code set} operation
	 * @throws IllegalArgumentException      (<a href="Collection.html#optional-restrictions">optional</a>)
	 *                                       if the comparator is found to violate the {@link Comparator}
	 *                                       contract
	 * @implSpec The default implementation obtains an array containing all elements in
	 * this list, sorts the array, and iterates over this list resetting each
	 * element from the corresponding position in the array. (This avoids the
	 * n<sup>2</sup> log(n) performance that would result from attempting
	 * to sort a linked list in place.)
	 * @implNote This implementation is a stable, adaptive, iterative mergesort that
	 * requires far fewer than n lg(n) comparisons when the input array is
	 * partially sorted, while offering the performance of a traditional
	 * mergesort when the input array is randomly ordered.  If the input array
	 * is nearly sorted, the implementation requires approximately n
	 * comparisons.  Temporary storage requirements vary from a small constant
	 * for nearly sorted input arrays to n/2 object references for randomly
	 * ordered input arrays.
	 *
	 * <p>The implementation takes equal advantage of ascending and
	 * descending order in its input array, and can take advantage of
	 * ascending and descending order in different parts of the same
	 * input array.  It is well-suited to merging two or more sorted arrays:
	 * simply concatenate the arrays and sort the resulting array.
	 *
	 * <p>The implementation was adapted from Tim Peters's list sort for Python
	 * (<a href="http://svn.python.org/projects/python/trunk/Objects/listsort.txt">
	 * TimSort</a>).  It uses techniques from Peter McIlroy's "Optimistic
	 * Sorting and Information Theoretic Complexity", in Proceedings of the
	 * Fourth Annual ACM-SIAM Symposium on Discrete Algorithms, pp 467-474,
	 * January 1993.
	 * @since 1.8
	 */
	@Override
	public void sort(Comparator<? super E> c) {

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

	/**
	 * Removes all of the elements of this collection that satisfy the given
	 * predicate.  Errors or runtime exceptions thrown during iteration or by
	 * the predicate are relayed to the caller.
	 *
	 * @param filter a predicate which returns {@code true} for elements to be
	 *               removed
	 * @return {@code true} if any elements were removed
	 * @throws NullPointerException          if the specified filter is null
	 * @throws UnsupportedOperationException if elements cannot be removed
	 *                                       from this collection.  Implementations may throw this exception if a
	 *                                       matching element cannot be removed or if, in general, removal is not
	 *                                       supported.
	 * @implSpec The default implementation traverses all elements of the collection using
	 * its {@link #iterator}.  Each matching element is removed using
	 * {@link Iterator#remove()}.  If the collection's iterator does not
	 * support removal then an {@code UnsupportedOperationException} will be
	 * thrown on the first matching element.
	 * @since 1.8
	 */
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return false;
	}

	/**
	 * Performs the given action for each element of the {@code Iterable}
	 * until all elements have been processed or the action throws an
	 * exception.  Unless otherwise specified by the implementing class,
	 * actions are performed in the order of iteration (if an iteration order
	 * is specified).  Exceptions thrown by the action are relayed to the
	 * caller.
	 *
	 * @param action The action to be performed for each element
	 * @throws NullPointerException if the specified action is null
	 * @implSpec <p>The default implementation behaves as if:
	 * <pre>{@code
	 *     for (T t : this)
	 *         action.accept(t);
	 * }</pre>
	 * @since 1.8
	 */
	@Override
	public void forEach(Consumer<? super E> action) {

	}

	/**
	 * Creates a {@code Spliterator} over the elements in this set.
	 *
	 * <p>The {@code Spliterator} reports {@link Spliterator#DISTINCT}.
	 * Implementations should document the reporting of additional
	 * characteristic values.
	 *
	 * @return a {@code Spliterator} over the elements in this set
	 * @implSpec The default implementation creates a
	 * <em><a href="Spliterator.html#binding">late-binding</a></em> spliterator
	 * from the set's {@code Iterator}.  The spliterator inherits the
	 * <em>fail-fast</em> properties of the set's iterator.
	 * <p>
	 * The created {@code Spliterator} additionally reports
	 * {@link Spliterator#SIZED}.
	 * @implNote The created {@code Spliterator} additionally reports
	 * {@link Spliterator#SUBSIZED}.
	 * @since 1.8
	 */
	@Override
	public Spliterator<E> spliterator() {
		return super.spliterator();
	}

	/**
	 * Returns a sequential {@code Stream} with this collection as its source.
	 *
	 * <p>This method should be overridden when the {@link #spliterator()}
	 * method cannot return a spliterator that is {@code IMMUTABLE},
	 * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
	 * for details.)
	 *
	 * @return a sequential {@code Stream} over the elements in this collection
	 * @implSpec The default implementation creates a sequential {@code Stream} from the
	 * collection's {@code Spliterator}.
	 * @since 1.8
	 */
	@Override
	public Stream<E> stream() {
		return null;
	}

	/**
	 * Returns a possibly parallel {@code Stream} with this collection as its
	 * source.  It is allowable for this method to return a sequential stream.
	 *
	 * <p>This method should be overridden when the {@link #spliterator()}
	 * method cannot return a spliterator that is {@code IMMUTABLE},
	 * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
	 * for details.)
	 *
	 * @return a possibly parallel {@code Stream} over the elements in this
	 * collection
	 * @implSpec The default implementation creates a parallel {@code Stream} from the
	 * collection's {@code Spliterator}.
	 * @since 1.8
	 */
	@Override
	public Stream<E> parallelStream() {
		return null;
	}
}
