package org.riotfamily.statistics.domain;

public class SimpleStatistics extends NamedEntity {
	private String value;

	public SimpleStatistics() {
		this(null, null);
	}

	public SimpleStatistics(String name, String value) {
		super(name);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
