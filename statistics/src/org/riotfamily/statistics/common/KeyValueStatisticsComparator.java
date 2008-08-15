package org.riotfamily.statistics.common;

import java.util.Comparator;

import org.riotfamily.riot.dao.Order;
import org.riotfamily.statistics.domain.SimpleStatistics;

public class KeyValueStatisticsComparator implements Comparator{

	private Order order;
	
	public KeyValueStatisticsComparator(Order order) {
		this.order = order;
	}
	
	public int compare(Object o1, Object o2) {
		SimpleStatistics s1 = (SimpleStatistics) o1;
		SimpleStatistics s2 = (SimpleStatistics) o2;
		Comparable t1;
		Comparable t2;
		if ("value".equalsIgnoreCase(order.getProperty())) {
			t1 = s1.getValue();
			t2 = s2.getValue();
		} else if ("name".equalsIgnoreCase(order.getProperty())){
			t1 = s1.getName();
			t2 = s2.getName();
		} else {
			t1 = new Integer(s1.getIdx());
			t2 = new Integer(s2.getIdx());
		}
		if (order.isCaseSensitive() && (t1 instanceof String)) {
			t1 = ((String)t1).toLowerCase();
			t2 = ((String)t2).toLowerCase();
		} 
		int inverse = order.isAscending() ? 1 : -1;
		int compareResult = t1.compareTo(t2);
		return compareResult * inverse;
	}

}
