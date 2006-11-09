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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.pages.page.support.AbstractPage;
import org.riotfamily.pages.page.support.PageComparator;

public class PersistentPage extends AbstractPage implements Cloneable {
	
	private static Log log = LogFactory.getLog(PersistentPage.class);
	
	private Long id;
	
	private Collection persistentChildPages;
	
	private transient Collection childPages;
	
	
	public PersistentPage() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
		
	public void initPosition(Collection siblings) {
		int maxPos = -1;
		if (siblings != null) {
			Iterator it = siblings.iterator();
			while (it.hasNext()) {
				Page sibling = (Page) it.next();
				maxPos = Math.max(maxPos, sibling.getPosition());
			}
		}
		setPosition(maxPos + 1);
	}


	public Collection getPersistentChildPages() {
		return this.persistentChildPages;
	}
	
	public void setPersistentChildPages(Collection childPages) {
		this.persistentChildPages = childPages;
	}
	
	public void setChildPages(Collection childPages) {
		Iterator it = childPages.iterator();
		while (it.hasNext()) {
			addChildPage((Page) it.next());
		}
	}
	
	public void addChildPage(Page child) {
		child.setParentAndUpdateChildPages(this);
		child.updatePath();
		if (!getChildPages().contains(child)) {
			if (child instanceof PersistentPage) {
				PersistentPage persistentChild = (PersistentPage) child;
				if (persistentChildPages == null) {
					persistentChildPages = new TreeSet(PageComparator.INSTANCE);
				}
				persistentChild.initPosition(getChildPages());
				persistentChildPages.add(persistentChild);
			}
			getChildPages().add(child);
		}
	}
	
	public void removeChildPage(Page child ) {
		if (child != null) {
			if (childPages != null) {
				childPages.remove(child);
			}
			if (persistentChildPages != null) {
				persistentChildPages.remove(child);
			}
		}
	}
	
	public Collection getChildPages() {
		if (childPages == null) {
			childPages = new ArrayList();
			try {
				if (persistentChildPages != null) {
					childPages.addAll(persistentChildPages);
				}
			}
			catch (RuntimeException e) {
				log.error(this, e);
				throw e;
			}
		}
		return childPages;
	}

	public void setParentAndUpdateChildPages(Page parent) {
		super.setParentAndUpdateChildPages(parent);
		if (persistentChildPages != null) {
			Iterator it = persistentChildPages.iterator();
			while (it.hasNext()) {
				PersistentPage child = (PersistentPage) it.next();
				child.updatePath();
			}
		}
	}
		
	public int hashCode() {
		return getPath().hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof PersistentPage) {
			PersistentPage that = (PersistentPage) obj;
			return this.getPath().equals(that.getPath());
		}
		return false;
	}
	
	public PersistentPage copy() {
		try {
			PersistentPage copy = (PersistentPage) clone();
			copy.id = null;
			copy.childPages = null;
			copy.persistentChildPages = null;
			return copy;
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException();
		}
	}
}
