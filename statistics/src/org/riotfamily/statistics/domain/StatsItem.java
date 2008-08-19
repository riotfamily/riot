package org.riotfamily.statistics.domain;

public class StatsItem {
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StatsItem() {
		this(null);
	}
	
	public StatsItem(String name) {
		this.name = name;
	}

}
