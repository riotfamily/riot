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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.hibernate.ActiveRecordSupport;
import org.riotfamily.common.hibernate.Lifecycle;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.config.SitemapSchema;
import org.springframework.beans.factory.annotation.Required;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_pages", uniqueConstraints = {@UniqueConstraint(columnNames={"site_id", "path"})})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
public class Page extends ActiveRecordSupport implements SiteMapItem, Lifecycle {

	public static final String TITLE_PROPERTY = "title";
	
	private String pageType;
	
	private Page parentPage;
	
	private long position;
	
	private Set<Page> childPages;
	
	private Page masterPage;
	
	private Set<Page> translations;
	
	private Site site;

	private String pathComponent;

	private boolean hidden;

	private boolean folder;

	private String path;
	
	private boolean published;

	private Date creationDate;

	private PageProperties pageProperties;
	
	private CacheService cacheService;
	
	private SitemapSchema schema;
	
	public Page() {
	}

	public Page(String pathComponent, Site site) {
		this.pathComponent = pathComponent;
		this.site = site;
	}

	public Page(Page master) {
		this.masterPage = master;
		this.creationDate = new Date();
		this.pageType = master.getPageType();
		this.pathComponent = master.getPathComponent();
		this.folder = master.isFolder();
		this.hidden = master.isHidden();
	}
	
	@Required	
	@Transient
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	
	@Required	
	@Transient
	public void setSchema(SitemapSchema schema) {
		this.schema = schema;
	}
	
	@Transient
	public String getCacheTag() {
		return Page.class.getName() + "#" + getId();
	}
	
	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	@ManyToOne(cascade=CascadeType.MERGE)
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@OneToMany
    @JoinColumn(name="parent_id")
    @OrderBy("position")
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public Set<Page> getChildPages() {
		return childPages;
	}

	public void setChildPages(Set<Page> childPages) {
		this.childPages = childPages;
	}
	
	@Column(name="pos")
	public long getPosition() {
		if (position == 0) {
			position = System.currentTimeMillis();
		}
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
	@JoinColumn(name="parent_id", insertable=false, updatable=false)
	public Page getParentPage() {
		return parentPage;
	}

	public void setParentPage(Page parentPage) {
		this.parentPage = parentPage;
	}

	@Transient
	public SiteMapItem getParent() {
		return parentPage != null ? parentPage : site;
	}
	
	@ManyToOne(cascade=CascadeType.MERGE)
	public Page getMasterPage() {
		return masterPage;
	}

	public void setMasterPage(Page masterPage) {
		this.masterPage = masterPage;
	}
		
	@OneToMany(mappedBy="masterPage", cascade={CascadeType.MERGE, CascadeType.PERSIST})
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public Set<Page> getTranslations() {
		return translations;
	}

	public void setTranslations(Set<Page> translations) {
		this.translations = translations;
	}

	@Transient
	public Locale getLocale() {
		return site.getLocale();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getPathComponent() {
		return pathComponent;
	}

	public void setPathComponent(String pathComponent) {
		this.pathComponent = pathComponent;
	}
	
	public boolean isHidden() {
		return this.hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	/**
	 * Returns whether the page only acts as container for other pages and
	 * has no own content.
	 */
	public boolean isFolder() {
		return this.folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Transient
	public String getUrl() {
		StringBuilder url = new StringBuilder();
		if (site.getPathPrefix() != null) {
			url.append(site.getPathPrefix());
		}
		url.append(getPath());
		String suffix = schema.getDefaultSuffix(pageType);
		if (suffix != null) {
			url.append(suffix);
		}
		return url.toString();
	}	
	
	public String getAbsoluteUrl(boolean secure, String defaultHost, String contextPath) {
		return site.makeAbsolute(secure, defaultHost, contextPath, getUrl());
	}
	
	@Transient
	public Collection<Page> getAncestors() {
		LinkedList<Page> pages = new LinkedList<Page>();
		Page page = this;
		while (page != null) {
			pages.addFirst(page);
			page = page.getParentPage();
		}
		return pages;
	}

		

	@ManyToOne(cascade=CascadeType.ALL)
	public PageProperties getPageProperties() {
		if (pageProperties == null) {
			pageProperties = new PageProperties(this);
		}
		return pageProperties;
	}

	public void setPageProperties(PageProperties pageProperties) {
		this.pageProperties = pageProperties;
	}
	
	
	@Transient
	public String getTitle() {
		return getTitle(true);
	}
	
	public String getTitle(boolean preview) {
		Object title = getPageProperties().unwrap(preview).get(TITLE_PROPERTY);
		if (title != null) {
			return title.toString();
		}
		return FormatUtils.xmlToTitleCase(pathComponent);
	}
	
	@Transient
	public boolean isDirty() {
		return getPageProperties().isDirty();
	}

	public boolean isPublished() {
		return this.published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	@Transient
	public boolean isRequestable() {
		return (published && site.isEnabled())
			|| AccessController.isAuthenticatedUser();
	}

	public boolean isVisible(boolean preview) {
		return !isHidden() 
				&& (published || preview)
				&& (!folder || hasVisibleChildPage(preview));
	}
	
	private boolean hasVisibleChildPage(boolean preview) {
		for (Page page : getChildPages()) {
			if (page.isVisible(preview)) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return site + ":" + path;
	}
	
	@Transient
	public Collection<Page> getSiblings() {
		return getParent().getChildPages();
	}
	
	
	@Transient
	public Collection<Page> getChildPagesWithFallback() {
		List<Page> pages = Generics.newArrayList();
		pages.addAll(getChildPages());
		pages.addAll(getTranslationCandidates());
		return pages;
	}
	
	@Transient
	public Collection<Page> getTranslationCandidates() {
		List<Page> candidates = Generics.newArrayList();
		Page master = getMasterPage();
		if (master != null) {
			for (Page page : master.getChildPages()) {
				boolean translated = false;
				for (Page child : getChildPages()) {
					if (page.equals(child.getMasterPage())) {
						translated = true;
						break;
					}
				}
				if (!translated) {
					candidates.add(page);
				}
			}			
		}
		return candidates;
	}
	
	@Transient
	public Object getHandler() {
		return schema.getPageType(pageType).getHandler();
	}

	// ----------------------------------------------------------------------
	// 
	// ----------------------------------------------------------------------
	
	public void addPage(Page child) {
		/*
		if (!PageValidationUtils.isValidChild(this, child)) {
			throw new DuplicatePathComponentException(
					"Page '{0}' did not validate", child.toString());
		}
		*/
		getChildPages().add(child);
		child.setSite(getSite());
		//child.setCreationDate(new Date());
		//deleteAlias(page);
		getParent().invalidateCacheItems();
	}
	
	public void removePage(Page child) {
		getChildPages().remove(child);
	}
	
	public void publish() {
		setPublished(true);
		if (getPageProperties().isDirty()) {
			getPageProperties().publish();
		}
		invalidateCacheItems();
		getParent().invalidateCacheItems();
	}
	
	public void unpublish() {
		setPublished(false);
		invalidateCacheItems();
		getParent().invalidateCacheItems();
	}
		
	public void invalidateCacheItems() {
		if (cacheService != null) {
			cacheService.invalidateTaggedItems(getCacheTag());
		}
	}

	private String buildPath() {
		StringBuffer path = new StringBuffer();
		Page page = this;
		while (page != null) {
			path.insert(0, page.getPathComponent());
			path.insert(0, '/');
			page = page.getParentPage();
		}
		return path.toString();
	}
	
	private void updatePath() {
		String path = buildPath();
		if (!path.equals(this.path)) {
			this.path = path;
			if (childPages != null) {
				for (Page child : childPages) {
					child.updatePath();
				}
			}
		}
	}
	
	// ----------------------------------------------------------------------
	// Implementation of the Lifecycle interface
	// ----------------------------------------------------------------------
	
	public void onSave() {
		updatePath();
	}
	
	public void onUpdate(Object oldState) {
		updatePath();
	}
	
	public void onDelete() {
	}
	
	// ----------------------------------------------------------------------
	// Persistence methods
	// ----------------------------------------------------------------------
	
	public static Page load(Long id) {
		return load(Page.class, id);
	}

	public void refreshIfDetached() {
		Session session = getSession();
		if (!session.contains(this)) {
			session.refresh(this);
		}
	}
	
	public static Page loadBySiteAndPath(Site site, String path) {
		return load("from Page where site = ? and path = ?", site, path);
	}
	
	public static Page loadByTypeAndSite(String pageType, Site site) {
		return load("from Page where pageType = ? and site = ?", pageType, site);
	}
		
	public static List<Page> findRootPagesBySite(Site site) {
		return find("from Page where parentPage is null and site = ? order by position", site);
	}

}
