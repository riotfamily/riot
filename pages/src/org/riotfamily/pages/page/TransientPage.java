package org.riotfamily.pages.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.pages.page.support.AbstractPage;

/**
 * Non-persitent page. Transient pages are usually created by a 
 * {@link PageMapPostProcessor} and used to display dynamic content.
 */
public class TransientPage extends AbstractPage {
	
	private List childPages;
	
	public TransientPage() {
	}
	
	public Collection getChildPages() {
		return this.childPages;
	}

	public void setChildPages(List childPages) {
		this.childPages = childPages;
	}

	public void addChildPage(Page child) {
		if (childPages == null) {
			childPages = new ArrayList();
		}
		childPages.add(child);
		child.setParent(this);
		child.updatePath();
	}
	
	protected Page getChildPage(String pathComponent) {
		if (childPages != null) {
			Iterator it = childPages.iterator();
			while (it.hasNext()) {
				Page child = (Page) it.next();
				if (child.getPathComponent().equals(pathComponent)) {
					return child;
				}
			}
		}
		return null;
	}
	
	public void removeChildPage(String pathComponent) {
		Page child = getChildPage(pathComponent);
		if (child != null) {
			childPages.remove(child);
		}
	}

}
