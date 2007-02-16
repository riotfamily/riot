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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.menu;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller that renders a navigation menu.
 */
public class MenuController extends AbstractMenuController {
		
	private int level = 0;
	
	private int depth = 0;
		
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}

	protected List processItems(List items, HttpServletRequest request) {
		if (!items.isEmpty()) {
			items = stripHigherLevels(items, request);
			limitDepth(items);
		}
		return items;
	}
	
	protected List stripHigherLevels(List items, HttpServletRequest request) {
		MenuItem item = null;
		for (int level = 0; level < this.level && items != null; level++) {
			Iterator it = items.iterator();
			items = null;
			while (it.hasNext()) {
				item = (MenuItem) it.next();
				if (item.isExpanded()) {
					items = item.getChildItems();
					break;
				}
			}
		}
		return items;
	}
	
	protected void limitDepth(List items) {
		for (int level = 0; level < this.depth && items != null; level++) {
			Iterator it = items.iterator();
			while (it.hasNext()) {
				MenuItem item = (MenuItem) it.next();
				if (item.isExpanded()) {
					if (level == this.depth - 1) {
						item.setChildItems(null);
					}
					else {
						items = item.getChildItems();
					}
					break;
				}
			}
		}
	}
	
}
