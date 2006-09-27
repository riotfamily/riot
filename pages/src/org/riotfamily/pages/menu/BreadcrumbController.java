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
