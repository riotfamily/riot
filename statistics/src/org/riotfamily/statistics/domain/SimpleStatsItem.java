package org.riotfamily.statistics.domain;

public class SimpleStatsItem extends StatsItem {
	
	private Object value;

	public SimpleStatsItem() {
		this(null, null);
	}

	public SimpleStatsItem(String name, Object value) {
		super(name);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
