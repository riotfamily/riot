package org.riotfamily.pages.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.page.meta.MetaData;
import org.riotfamily.pages.page.meta.MetaDataProvider;
import org.riotfamily.pages.page.support.AbstractPage;

/**
 * Non-persitent page. Transient pages are usually created by a 
 * {@link PageMapPostProcessor} and used to display dynamic content.
 */
public class TransientPage extends AbstractPage {
	
	private List childPages;
	
	private MetaData metaData;
	
	private MetaDataProvider metaDataProvider;
	
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
	
	public MetaDataProvider getMetaDataProvider() {
		return this.metaDataProvider;
	}

	public void setMetaDataProvider(MetaDataProvider metaDataProvider) {
		this.metaDataProvider = metaDataProvider;
	}

	public MetaData resolveMetaData(HttpServletRequest request) {
		if (metaData != null) {
			return metaData;
		}
		if (metaDataProvider != null) {
			return metaDataProvider.getMetaData(this, request);
		}
		return null;
	}
	
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

}
