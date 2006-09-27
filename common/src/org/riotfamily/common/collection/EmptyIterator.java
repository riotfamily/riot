package org.riotfamily.common.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator with no elements.
 */
public class EmptyIterator implements Iterator {

	public static final Iterator INSTANCE = new EmptyIterator();
	
	protected EmptyIterator() {
	}
	
	public boolean hasNext() {
		return false;
	}

	public Object next() {
		throw new NoSuchElementException("Iterator contains no elements");
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
