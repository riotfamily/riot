package org.riotfamily.common.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator with no elements.
 */
public class EmptyIterator<T> implements Iterator<T> {

	protected EmptyIterator() {
	}
	
	public boolean hasNext() {
		return false;
	}

	public T next() {
		throw new NoSuchElementException("Iterator contains no elements");
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
