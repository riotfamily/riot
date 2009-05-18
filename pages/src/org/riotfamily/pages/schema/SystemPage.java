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
package org.riotfamily.pages.schema;

import java.util.List;
import java.util.Map;

import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageProperties;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.model.SiteMapItem;
import org.springframework.util.Assert;

public class SystemPage {

	private String systemId;
	
	private String pathComponent;
	
	private boolean folder;
	
	private TypeInfo type;
	
	private List<SystemPage> childPages;
	
	private Map<String, Object> properties;

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getPathComponent() {
		if (pathComponent == null) {
			return systemId;
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

	public TypeInfo getType() {
		if (type == null) {
			type = new TypeInfo(systemId);
		}
		return type;
	}

	public void setType(TypeInfo type) {
		this.type = type;
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
		Page page = Page.loadBySystemIdAndSite(systemId, parent.getSite());
		if (page == null) {
			page = createPage(parent.getSite());
			parent.addPage(page);
		}
		update(page);
	}
	
	private Page createPage(Site site) {
		Page page = new Page(getPathComponent(), site);
		page.setSystemId(systemId);
		page.setFolder(folder);
		//TODO page.setPageProperties(...);
		return page;
	}

	private void update(Page page) {
		page.setPageType(getType().getName());
		if (childPages != null) {
			for (SystemPage child : childPages) {
				child.sync(page);
			}
		}
	}

}
