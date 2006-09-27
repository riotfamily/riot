package org.riotfamily.riot.list.ui;


/**
 * Holds information about a list cell, namely the CSS class and the content.
 */
public class ListCell {

	private String cssClass;
	
	private String data;
	
	public ListCell(String cssClass, String data) {
		this.cssClass = cssClass;
		this.data = data;
	}
	
	public String getCssClass() {
		return cssClass;
	}
	
	public String getData() {
		return data;
	}
}
