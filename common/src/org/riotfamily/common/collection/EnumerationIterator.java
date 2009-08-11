package org.riotfamily.common.collection;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator<T> implements Iterator<T>, Iterable<T> {

	private Enumeration<T> enumeration;

	public EnumerationIterator(Enumeration<T> enumeration) {
		this.enumeration = enumeration;
	}

	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	public T next() {
		return enumeration.nextElement();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<T> iterator() {
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public static<T> EnumerationIterator<T> create(Enumeration enumeration) {
		return new EnumerationIterator<T>(enumeration);
	}
	
}
