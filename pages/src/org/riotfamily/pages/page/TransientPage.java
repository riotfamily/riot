package org.riotfamily.pages.page;

import java.util.ArrayList;
import java.util.Collection;
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
	
	public void removeChildPage(Page child) {
		if (child != null) {
			childPages.remove(child);
		}
	}

}
