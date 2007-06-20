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
package org.riotfamily.pages;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.VersionContainer;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class Page {

	private Long id;

	private PageNode node;

	private Locale locale;

	private String pathComponent;

	private boolean folder;

	private String path;

	private boolean published;

	private Date creationDate;

	private VersionContainer versionContainer;

	public Page() {
	}

	public Page(String pathComponent, Locale locale) {
		this.pathComponent = pathComponent;
		this.locale = locale;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PageNode getNode() {
		return this.node;
	}

	public void setNode(PageNode node) {
		this.node = node;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
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

	public boolean isWildcardMapping() {
		return "*".equals(pathComponent);
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
		if (path == null) {
			path = buildPath();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String buildPath() {
		StringBuffer path = new StringBuffer();
		Page page = this;
		while (page != null) {
			path.insert(0, page.getPathComponent());
			path.insert(0, '/');
			page = page.getParentPage();
		}
		return path.toString();
	}

	public Page getParentPage() {
		PageNode parentNode = node.getParent();
		if (parentNode == null) {
			return null;
		}
		return parentNode.getPage(locale);
	}

	public Collection getChildPages() {
		return node.getChildPages(locale);
	}

	public Collection getChildPages(Locale fallbackLocale) {
		return node.getChildPages(locale, fallbackLocale);
	}

	public Collection getAncestors() {
		LinkedList pages = new LinkedList();
		Page page = this;
		while (page != null) {
			pages.addFirst(page);
			page = page.getParentPage();
		}
		return pages;
	}

	public Collection getSiblings() {
		return node.getParent().getChildPages(locale);
	}

	public void addChildPage(Page child) {
		child.setLocale(locale);
		node.addChildNode(new PageNode(child));
	}

	public String getHandlerName() {
		return node.getHandlerName();
	}

	public VersionContainer getVersionContainer() {
		if (versionContainer == null) {
			versionContainer = new VersionContainer();
			ComponentVersion version = new ComponentVersion(Page.class.getName());
			versionContainer.setLiveVersion(version);
		}
		return versionContainer;
	}

	public void setVersionContainer(VersionContainer versionContainer) {
		this.versionContainer = versionContainer;
	}

	public Map getProperties(boolean preview) {
		if (preview) {
			return getVersionContainer().getLatestVersion().getProperties();
		}
		ComponentVersion version = getVersionContainer().getLiveVersion();
		return version != null ? version.getProperties() : null;
	}

	public String getProperty(String key, boolean preview) {
		ComponentVersion version = preview
				? getVersionContainer().getLatestVersion()
				: getVersionContainer().getLiveVersion();

		return version != null ? version.getProperty(key) : null;
	}

	public boolean isDirty() {
		return getVersionContainer().isDirty();
	}

	public boolean isPublished() {
		return this.published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public boolean isEnabled() {
		return published && getNode().getSite().isLocaleEnabled(locale);
	}

	public String toString() {
		return locale + ":" + path;
	}
}
