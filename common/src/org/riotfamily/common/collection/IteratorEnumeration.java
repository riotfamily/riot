package org.riotfamily.common.collection;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration implements Enumeration {

	private Iterator iterator;
	
	public IteratorEnumeration(Iterator iterator) {
		this.iterator = iterator;
	}

	public boolean hasMoreElements() {
		return iterator.hasNext();
	}
	
	public Object nextElement() {
		return iterator.next();
	}

}
