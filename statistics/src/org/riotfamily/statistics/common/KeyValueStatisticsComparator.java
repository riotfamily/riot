package org.riotfamily.statistics.common;

import java.util.Comparator;

import org.riotfamily.riot.dao.Order;
import org.riotfamily.statistics.domain.SimpleStatistics;

public class KeyValueStatisticsComparator implements Comparator<SimpleStatistics> {

	private Order order;
	
	public KeyValueStatisticsComparator(Order order) {
		this.order = order;
	}
	
	public int compare(SimpleStatistics o1, SimpleStatistics o2) {
		int compareResult;
		if ("value".equalsIgnoreCase(order.getProperty())) {
			String s1 = o1.getValue();
			String s2 = o2.getValue();
			if (order.isCaseSensitive()) {
				s1 = s1.toLowerCase();
				s2 = s2.toLowerCase();
			}
			compareResult = s1.compareTo(s2);
		} 
		else if ("name".equalsIgnoreCase(order.getProperty())) {
			String s1 = o1.getName();
			String s2 = o2.getName();
			if (order.isCaseSensitive()) {
				s1 = s1.toLowerCase();
				s2 = s2.toLowerCase();
			}
			compareResult = s1.compareTo(s2);
		} 
		else {
			int i1 = o1.getIdx();
			int i2 = o2.getIdx();
			compareResult = i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
		}
		
		int inverse = order.isAscending() ? 1 : -1;
		return compareResult * inverse;
	}

}
