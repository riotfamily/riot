package org.riotfamily.riot.dao;

import java.io.Serializable;

public class Order implements Serializable {

	String property;

	boolean ascending;
	
	boolean caseSensitive;

	
	public Order(String property, boolean ascending, boolean caseSensitive) {
		this.property = property;
		this.ascending = ascending;
		this.caseSensitive = caseSensitive;
	}

	public String getProperty() {
		return property;
	}

	public boolean isAscending() {
		return ascending;
	}	
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isProperty(String property) {
		return this.property.equals(property);
	}
	
	public void toggleDirection() {
		ascending = !ascending;
	}
	
}