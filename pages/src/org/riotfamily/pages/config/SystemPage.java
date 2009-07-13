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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.config;

import java.util.List;
import java.util.Map;

import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.model.SiteMapItem;

public class SystemPage extends PageType {

	private String pathComponent;
	
	private boolean folder;
	
	private List<SystemPage> childPages;
	
	private Map<String, Object> properties;

	public String getPathComponent() {
		if (pathComponent == null) {
			return getName();
		}
		return pathComponent;
	}

	public void setPathComponent(String pathComponent) {
		this.pathComponent = pathComponent;
	}

	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public List<SystemPage> getChildPages() {
		return childPages;
	}

	public void setChildPages(List<SystemPage> childPages) {
		this.childPages = childPages;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public void sync(SiteMapItem parent) {
		Page page = Page.loadByTypeAndSite(getName(), parent.getSite());
		if (page == null) {
			page = createPage(parent.getSite());
			parent.addPage(page);
		}
		update(page);
	}
	
	private Page createPage(Site site) {
		Page page = new Page(getPathComponent(), site);
		page.setFolder(folder);
		page.getPageProperties().getPreviewVersion().wrap(properties);
		return page;
	}

	private void update(Page page) {
		page.setPageType(getName());
		if (childPages != null) {
			for (SystemPage child : childPages) {
				child.sync(page);
			}
		}
	}

	@Override
	void register(SitemapSchema schema) {
		super.register(schema);
		schema.addSystemPage(this);
		if (childPages != null) {
			for (SystemPage child : childPages) {
				child.register(schema);
			}
		}
	}
}
