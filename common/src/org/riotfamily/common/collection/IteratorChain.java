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

import org.riotfamily.common.util.Generics;

public class IteratorChain<T> implements Iterator<T> {
	
	private List<Iterator<T>> iterators = Generics.newArrayList();
	
	private Iterator<T> currentIterator;
	
	private int index = 0;
	
	private boolean locked = false;

	public IteratorChain() {
	}
	
	public IteratorChain(Iterator<T> it) {
		add(it);
	}
	
	public IteratorChain(Collection<Iterator<T>> iterators) {
		addAll(iterators);
	}

	public void add(Iterator<T> it) {
		if (locked) {
			throw new UnsupportedOperationException("Chain can not be modified after first use of Iterator Interface");
		}
		iterators.add(it);
	}
	
	public void addAll(Collection<Iterator<T>> iterators) {
		if (locked) {
			throw new UnsupportedOperationException("Chain can not be modified after first use of Iterator Interface");
		}
		iterators.addAll(iterators);
	}

	private void lock() {
		if (!locked) {
			locked = true;
		}
	}
	
	private void update() {
		if (currentIterator == null) {
			if (iterators.isEmpty()) {
				currentIterator = new EmptyIterator<T>();
			}
			else {
				currentIterator = iterators.get(0);
			}
		}
		if (!currentIterator.hasNext() && index < iterators.size() - 1) {
			index++;
			currentIterator = iterators.get(index);
		}
	}

	public boolean hasNext() {
		lock();
		update();
		return currentIterator.hasNext();
	}

	public T next() {
		lock();
		update();
		return currentIterator.next();
	}

	public void remove() {
		lock();
		if (currentIterator == null) {
			update();
		}
		currentIterator.remove();
	}

}
