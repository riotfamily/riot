package org.riotfamily.forms.element.collection;

import java.util.Comparator;

class ListItemComparator implements Comparator<ListItem> {

	private String itemOrder;
	
	public ListItemComparator(String itemOrder) {
		this.itemOrder = itemOrder;
	}

	public int compare(ListItem item1, ListItem item2) {
		int pos1 = itemOrder.indexOf(item1.getId() + ',');
		int pos2 = itemOrder.indexOf(item2.getId() + ',');
		return pos1 - pos2;
	}
	
}