/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.collection;

import java.util.Comparator;

class ListItemComparator implements Comparator {

	private String itemOrder;
	
	public ListItemComparator(String itemOrder) {
		this.itemOrder = itemOrder;
	}

	public int compare(Object obj1, Object obj2) {
		ListItem item1 = (ListItem) obj1;
		ListItem item2 = (ListItem) obj2;
		int pos1 = itemOrder.indexOf(item1.getId() + ',');
		int pos2 = itemOrder.indexOf(item2.getId() + ',');
		return pos1 - pos2;
	}
	
}