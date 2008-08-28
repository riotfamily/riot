package org.riotfamily.statistics.domain;

public class SimpleStatsItem extends StatsItem {
	
	public static final String STYLE_CRITICAL = "critical";
	
	private Object value;

	private String style;
	
	public SimpleStatsItem() {
		this(null, null, false);
	}

	public SimpleStatsItem(String name, Object value, boolean critical) {
		super(name);
		this.value = value;
		this.style = critical ? STYLE_CRITICAL : null;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getStyle() {
		return style;
	}

}
