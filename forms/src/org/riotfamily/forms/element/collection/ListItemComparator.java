/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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