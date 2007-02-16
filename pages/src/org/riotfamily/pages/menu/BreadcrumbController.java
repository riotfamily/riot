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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller that renders a breadcrumb navigation path.
 */
public class BreadcrumbController extends AbstractMenuController {

		
	private int startLevel = 0;
		
	public void setStartLevel(int startLevel) {
		this.startLevel = startLevel;
	}

	protected List processItems(List items, HttpServletRequest request) {
		ArrayList breadcrumbs = new ArrayList();
		appendExpanded(items, 0, breadcrumbs);
		return breadcrumbs;
	}
	
	protected void appendExpanded(Collection items, int level, 
			List breadcrumbs) {
		
		if (items == null) {
			return;
		}
		Iterator it = items.iterator();
		while (it.hasNext()) {
			MenuItem item = (MenuItem) it.next();
			if (item.isExpanded()) {
				if (level >= this.startLevel) {
					breadcrumbs.add(item);
				}
				List childItems = item.getChildItems();
				item.setChildItems(null);
				appendExpanded(childItems, level+1, breadcrumbs);
				break;
			}
		}
	}

}
