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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Class that represents a node in the website's sitemap. Each PageNode has
 * a set of {@link Page pages} that hold localized data for a {@link Site}.
 * The PageNode itself holds the data that all pages have in common.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_page_nodes")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
public class PageNode {

	private Long id;

	private PageNode parent;

	private List<PageNode> childNodes;

	private Set<Page> pages;

	private String pageType;

	private boolean systemNode;

	private boolean hidden;

	public PageNode() {
	}

	/**
	 * Convenience constructor that {@link #addPage(Page) adds} the given Page.
	 */
	public PageNode(Page page) {
		addPage(page);
	}

	/**
	 * Returns the id of the PageNode. 
	 */
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Returns the parent node. There must be exactly one PageNode without
	 * a parent. This node can be obtained via the getRootNode() method of
	 * the PageDao.
	 */
	@ManyToOne
	@JoinColumn(name="parent_id", insertable=false, updatable=false)
	@Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	public PageNode getParent() {
		return this.parent;
	}

	/**
	 * Sets the parent node. To establish a parent-child use the 
	 * {@link #addChildNode(PageNode)} method.  
	 */
	protected void setParent(PageNode parent) {
		this.parent = parent;
	}

	/**
	 * Returns the set of {@link Page pages} associated with this node.
	 */
	@OneToMany(mappedBy="node")
	@Cascade({CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public Set<Page> getPages() {
		return pages;
	}
	
	public void setPages(Set<Page> pages) {
		this.pages = pages;
	}

	/**
	 * Adds a child node. If the given node has no handlerName, it will be
	 * set to the childHandlerName.
	 */
	public void addChildNode(PageNode node) {
		node.setParent(this);
		if (childNodes == null) {
			childNodes = new ArrayList<PageNode>();
		}
		childNodes.add(node);
	}

	/**
	 * Returns the child nodes. 
	 */
	@OneToMany
    @IndexColumn(name="node_pos")
    @JoinColumn(name="parent_id")
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public List<PageNode> getChildNodes() {
		return this.childNodes;
	}

	public void setChildNodes(List<PageNode> childNodes) {
		this.childNodes = childNodes;
	}
	
	/**
	 * Returns an unmodifiable list of all child pages that are available in
	 * the given site.
	 */
	public List<Page> getChildPages(Site site) {
		LinkedList<Page> pages = new LinkedList<Page>();
		if (childNodes != null) {
			for (PageNode childNode : childNodes) {
				Page page = childNode.getPage(site);
				if (page != null) {
					pages.add(page);
				}
			}
		}
		return Collections.unmodifiableList(pages);
	}

	/**
	 * Returns an unmodifiable list of all child pages available in the given
	 * site or its {@link Site#getMasterSite() master site}. This method is used
	 * by the PageRiotDao to list all pages are already localized or can be
	 * translated. 
	 */
	public Collection<Page> getChildPagesWithFallback(Site site) {
		LinkedList<Page> pages = new LinkedList<Page>();
		if (childNodes != null) {
			for (PageNode childNode : childNodes) {
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

	/**
	 * Returns the localized page for the given site or null if no translation
	 * is available. 
	 */
	public Page getPage(Site site) {
		if (pages == null) {
			return null;
		}
		for (Page page : pages) {
			if (ObjectUtils.nullSafeEquals(page.getSite(), site)) {
				return page;
			}
		}
		return null;
	}

	/**
	 * Adds a localized page to the node.
	 * @throws IllegalArgumentException 
	 *         If the given page is null or 
	 *         the page is not associated with a site or 
	 *         another page with the same site is already present
	 */
	public void addPage(Page page) {
		Assert.notNull(page, "The page must not be null.");
		Assert.notNull(page.getSite(), "The page must be associated with a " +
				"site before it can be added.");
		
		Assert.isNull(getPage(page.getSite()), "This node already has a " +
				" page for the site " + page.getSite());
		
		page.setNode(this);
		if (pages == null) {
			pages = new HashSet<Page>();
		}
		pages.add(page);
	}

	/**
	 * Removes the given page from the node.
	 */
	public void removePage(Page page) {
		pages.remove(page);
	}

	/**
	 * Returns whether the node has any pages.
	 */
	public boolean hasPages() {
		return !pages.isEmpty();
	}

	/**
	 * Returns the page type. The type is used to select an appropriate view.
	 */
	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
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
