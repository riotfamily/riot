package org.riotfamily.statistics.common;

import java.util.Comparator;

import org.riotfamily.riot.dao.Order;
import org.riotfamily.statistics.domain.NamedEntity;

public class NamedEntityComparator implements Comparator<NamedEntity> {

	private Order order;
	
	public NamedEntityComparator(Order order) {
		this.order = order;
	}
	
	public int compare(NamedEntity ne1, NamedEntity ne2) {
		int compareResult;
		if ("name".equalsIgnoreCase(order.getProperty())) {
			String s1 = ne1.getName();
			String s2 = ne2.getName();
			if (order.isCaseSensitive()) {
				s1 = s1.toLowerCase();
				s2 = s2.toLowerCase();
			}
			compareResult = s1.compareTo(s2);
		} 
		else {
			int i1 = ne1.getIdx();
			int i2 = ne2.getIdx();
			compareResult = i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
		}
		int inverse = order.isAscending() ? 1 : -1;
		return compareResult * inverse;
	}

}
