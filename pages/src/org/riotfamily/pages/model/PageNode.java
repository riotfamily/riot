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
package org.riotfamily.pages.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.ObjectUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class PageNode {

	private Long id;

	private PageNode parent;

	private List childNodes;

	private Set pages;

	private String handlerName;

	private boolean systemNode;

	private boolean hidden;

	public PageNode() {
	}

	public PageNode(Page page) {
		addPage(page);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PageNode getParent() {
		return this.parent;
	}

	public void setParent(PageNode parent) {
		this.parent = parent;
	}

	public Set getPages() {
		return pages;
	}

	public void setPages(Set pages) {
		this.pages = pages;
	}

	public void setChildNodes(List childNodes) {
		this.childNodes = childNodes;
	}

	/**
	 * Adds a child node. If the given node has no handlerName, it will be
	 * set to the childHandlerName.
	 */
	public void addChildNode(PageNode node) {
		node.setParent(this);
		if (childNodes == null) {
			childNodes = new ArrayList();
		}
		childNodes.add(node);
	}

	public List getChildNodes() {
		return this.childNodes;
	}

	public List getChildPages(Site site) {
		LinkedList pages = new LinkedList();
		if (childNodes != null) {
			Iterator it = childNodes.iterator();
			while (it.hasNext()) {
				PageNode childNode = (PageNode) it.next();
				Page page = childNode.getPage(site);
				if (page != null) {
					pages.add(page);
				}
			}
		}
		return Collections.unmodifiableList(pages);
	}

	public Collection getChildPagesWithFallback(Site site) {
		LinkedList pages = new LinkedList();
		if (childNodes != null) {
			Iterator it = childNodes.iterator();
			while (it.hasNext()) {
				PageNode childNode = (PageNode) it.next();
				Page page = childNode.getPage(site);
				if (page == null && site.getMasterSite() != null) {
					page = childNode.getPage(site.getMasterSite());
				}
				if (page != null) {
					pages.add(page);
				}
			}
		}
		return Collections.unmodifiableCollection(pages);
	}

	public Page getPage(Site site) {
		if (pages == null) {
			return null;
		}
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (ObjectUtils.nullSafeEquals(page.getSite(), site)) {
				return page;
			}
		}
		return null;
	}

	public void addPage(Page page) {
		page.setNode(this);
		if (pages == null) {
			pages = new HashSet();
		}
		pages.add(page);
	}

	public void removePage(Page page) {
		pages.remove(page);
	}

	public boolean hasPages() {
		return !pages.isEmpty();
	}

	/**
	 * Returns the name of the handler that will be used to serve the page.
	 */
	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	/**
	 * Returns whether the page should be hidden in menus.
	 */
	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Returns whether the node is a system node. System nodes are usually
	 * created by a PageSetupBean and provide some kind of functionality
	 * (in contrast to nodes that only contain content) and must not be deleted.
	 */
	public boolean isSystemNode() {
		return this.systemNode;
	}

	public void setSystemNode(boolean systemNode) {
		this.systemNode = systemNode;
	}

}
