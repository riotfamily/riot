package org.riotfamily.statistics.common;

import java.util.Comparator;

import org.riotfamily.riot.dao.Order;
import org.riotfamily.statistics.domain.NamedEntity;

public class NamedEntityComparator implements Comparator{

	private Order order;
	
	public NamedEntityComparator(Order order) {
		this.order = order;
	}
	
	public int compare(Object o1, Object o2) {
		NamedEntity ne1 = (NamedEntity) o1;
		NamedEntity ne2 = (NamedEntity) o2;
		Comparable t1;
		Comparable t2;
		if ("name".equalsIgnoreCase(order.getProperty())) {
			if (order.isCaseSensitive()) {
				t1 = ne1.getName();
				t2 = ne2.getName();
			} else {
				t1 = ne1.getName().toLowerCase();
				t2 = ne2.getName().toLowerCase();
			}
		} else {
			t1 = new Integer(ne1.getIdx());
			t2 = new Integer(ne2.getIdx());
		}
		int inverse = order.isAscending() ? 1 : -1;
		int compareResult = t1.compareTo(t2);
		return compareResult * inverse;
	}

}
