/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.pages.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.IndexColumn;
import org.riotfamily.common.hibernate.Lifecycle;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.cache.TagCacheItems;
import org.riotfamily.components.model.ContentEntity;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.pages.config.PageType;
import org.springframework.util.StringUtils;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_pages", uniqueConstraints = {@UniqueConstraint(columnNames={"site_id", "path"})})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
@TagCacheItems
public class ContentPage extends ContentEntity implements Page, Lifecycle {

	public static final String TITLE_PROPERTY = "title";
	
	private Long id;
	
	private String pageTypeName;
	
	private ContentPage parent;
	
	private List<ContentPage> children;
	
	private Site site;

	private String pathComponent;

	private String path;
	
	private Date creationDate;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public ContentPage() {
	}

	public ContentPage(String pathComponent, Site site) {
		this.pathComponent = pathComponent;
		this.site = site;
	}
		
	public String getPathComponent() {
		return pathComponent;
	}
	
	public void setPathComponent(String pathComponent) {
		this.pathComponent = pathComponent;
	}
	
	@Transient
	public PageType getPageType() {
		if (site != null && pageTypeName != null) {
			return site.getSchema().getPageType(pageTypeName);
		}
		return null;
	}

	public void setPageType(PageType pageType) {
		this.pageTypeName = pageType.getName();
	}
	
	@Column(name="pageType")
	protected String getPageTypeName() {
		return pageTypeName;
	}
	
	protected void setPageTypeName(String pageTypeName) {
		this.pageTypeName = pageTypeName;
	}
	
	@ManyToOne(cascade=CascadeType.MERGE)
	public Site getSite() {
		return this.site;
	}
	
	public void setSite(Site site) {
		this.site = site;
	}
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.MERGE)
	@JoinColumn(name="parent_id", updatable=false, insertable=false)
	public ContentPage getParent() {
		return parent;
	}
	
	public void setParent(ContentPage parent) {
		this.parent = parent;
	}
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="parent_id")
	@IndexColumn(name="pos")
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public List<ContentPage> getChildren() {
		return children;
	}
		
	public void setChildren(List<ContentPage> children) {
		this.children = children;
	}
			
	// ----------------------------------------------------------------------
	
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

	@Transient
	public String getTitle() {
		Object title = getContentContainer().getContent(true).get(TITLE_PROPERTY);
		if (title != null) {
			return title.toString();
		}
		if (!StringUtils.hasText(pathComponent)) {
			return "/";
		}
		return FormatUtils.xmlToTitleCase(pathComponent);
	}
	
	@Transient
	public boolean isRequestable() {
		return (isPublished() && site.isEnabled())
			|| AccessController.isAuthenticatedUser();
	}
		
	@Transient
	public List<ContentPage> getSiblings() {
		if (parent == null) {
			return Collections.singletonList(this);
		}
		return parent.getChildren();
	}
		
	@Override
	public String toString() {
		return String.format("ContentPage[path=%s,id=%s,site=%s]", 
				getPath(), getId(), site.getName());
	}
	
	// ----------------------------------------------------------------------
	// 
	// ----------------------------------------------------------------------
	
	public void addPage(ContentPage child) {
		/*
		if (!PageValidationUtils.isValidChild(this, child)) {
			throw new DuplicatePathComponentException(
					"Page '{0}' did not validate", child.toString());
		}
		*/
		child.setParent(this);
		child.setSite(getSite());
		if (children == null) {
			children = Generics.newArrayList();
		}
		children.add(child);
	}
	
	public void removePage(ContentPage child) {
		children.remove(child);
		if (this.equals(child.getParent())) {
			child.setParent(null);
		}
	}
		
	// ----------------------------------------------------------------------
	// Schema methods
	// ----------------------------------------------------------------------
	
	@Transient
	public boolean isSystemPage() {
		return site.getSchema().isSystemPage(this);
	}

	@Transient
	public boolean canHaveChildren() {
		return site.getSchema().canHaveChildren(this);
	}

	@Transient
	public boolean isValidChild(ContentPage child) {
		return site.getSchema().isValidChild(this, child);
	}

	// ----------------------------------------------------------------------
	// Materialized path methods
	// ----------------------------------------------------------------------
	
	public String getPath() {
		if (path == null) {
			materializePath();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void updatePath() {
		String oldPath = this.path;
		if (materializePath()) {
			PageAlias.create(this, oldPath);
			updateChildPaths();
		}
	}
	
	private void updateChildPaths() {
		if (children != null) {
			for (ContentPage child : children) {
				child.updatePath();
			}
		}
	}
	
	private boolean materializePath() {
		String path = buildPath();
		if (!path.equals(this.path)) {
			this.path = path;
			return true;
		}
		return false;
	}
	
	private String buildPath() {
		if (parent != null) {
			String s = parent.getPath();
			if (!s.endsWith("/")) {
				s += "/";
			}
			return s + pathComponent;
		}
		return "/";
	}
	
	// ----------------------------------------------------------------------
	// Implementation of the Lifecycle interface
	// ----------------------------------------------------------------------
	
	public void onSave() {
		setCreationDate(new Date());
		PageAlias.create(this, null);
		updateChildPaths();
	}
	
	public void onUpdate(Object oldState) {
		updatePath();
	}
	
	public void onDelete() {
		PageAlias.resetByPage(this);
	}
	
	// ----------------------------------------------------------------------
	// Persistence methods
	// ----------------------------------------------------------------------
	
	public static ContentPage load(Long id) {
		return load(ContentPage.class, id);
	}

	public void refreshIfDetached() {
		Session session = getSession();
		if (!session.contains(this)) {
			session.refresh(this);
		}
	}
	
	public static ContentPage loadBySiteAndPath(Site site, String path) {
		return query(ContentPage.class, 
				"from {} where site = ? and path = ?", site, path)
				.cache().load();
	}
	
	public static ContentPage loadByTypeAndSite(String pageType, Site site) {
		return query(ContentPage.class, 
				"from {} where pageType = ? and site = ?", pageType, site)
				.cache().load();
	}

	public static List<ContentPage> findByTypesAndSite(Collection<String> types, Site site) {
		if (types == null || types.isEmpty()) {
			return Collections.emptyList();
		}
		return query(ContentPage.class,
				"from {} where pageType in (:types) and site = :site")
				.setParameterList("types", types)
				.setParameter("site", site)
				.cache().find();
	}

}
