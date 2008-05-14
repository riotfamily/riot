package org.riotfamily.common.collection;

import java.util.Comparator;

public class ToStringComparator implements Comparator {
	
	public int compare(Object obj1, Object obj2) {
		if (obj1 == null) {
			if (obj2 == null) {
				return 0;
			}
			return Integer.MIN_VALUE;
		}
		if (obj2 == null) {
			return Integer.MAX_VALUE;
		}
		return obj1.toString().compareTo(obj2.toString());
	}

}
