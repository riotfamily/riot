package org.riotfamily.common.collection;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Enumeration that works on an Iterator.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class IteratorEnumeration<T> implements Enumeration<T> {

	private Iterator<T> iterator;
	
	public IteratorEnumeration(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	public boolean hasMoreElements() {
		return iterator.hasNext();
	}
	
	public T nextElement() {
		return iterator.next();
	}

}
