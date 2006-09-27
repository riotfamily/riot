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
