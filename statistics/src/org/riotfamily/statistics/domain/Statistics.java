package org.riotfamily.statistics.domain;

import java.util.List;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.springframework.util.ObjectUtils;

public class Statistics {

	private List<SimpleStatsItem> items = Generics.newArrayList();
	
	public void add(String name, Object value) {
		add(name, value, false);
	}
	
	public void add(String name, Object value, boolean critical) {
		items.add(new SimpleStatsItem(name, value, critical));
	}
	
	public void addOkIfNull(String name, Object value) {
		items.add(new SimpleStatsItem(name, value, value != null));
	}
	
	public void addOkIfEquals(String name, Object value, Object expected) {
		items.add(new SimpleStatsItem(name, value, !ObjectUtils.nullSafeEquals(value, expected)));
	}
	
	public void addOkBelow(String name, Number value, Number threshold) {
		items.add(new SimpleStatsItem(name, value, value.longValue() > threshold.longValue()));
	}
	
	public void addMillis(String name, long millis) {
		addMillis(name, millis, false);
	}
	
	public void addMillis(String name, long millis, boolean critical) {
		items.add(new SimpleStatsItem(name, FormatUtils.formatMillis(millis), critical));
	}
	
	public void addBytes(String name, long bytes) {
		addBytes(name, bytes, false);
	}
	
	public void addBytes(String name, long bytes, boolean critical) {
		items.add(new SimpleStatsItem(name, FormatUtils.formatByteSize(bytes), critical));
	}
		
	public List<SimpleStatsItem> getItems() {
		return items;
	}
}
