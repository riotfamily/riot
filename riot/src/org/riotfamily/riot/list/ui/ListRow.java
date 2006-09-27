package org.riotfamily.riot.list.ui;

import java.util.Collection;

/**
 * Holds information about a list row.
 */
public class ListRow {

	private String cssClass;
	
	private String objectId;

	private Collection cells;

	public ListRow(String cssClass, String objectId, Collection cells) {
		this.cssClass = cssClass;
		this.objectId = objectId;
		this.cells = cells;
	}

	public Collection getCells() {
		return cells;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getCssClass() {
		return this.cssClass;
	}
	
}
