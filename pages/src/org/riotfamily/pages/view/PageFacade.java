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
package org.riotfamily.pages.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.config.component.Component;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.VersionContainer;
import org.riotfamily.pages.mapping.PageUrlBuilder;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageFacade {

	private Page page;
	
	private boolean preview;
	
	private PageUrlBuilder pageUrlBuilder;
	
	private ComponentRepository componentRepository;
	
	private Map properties = null;
	
	public PageFacade(Page page, boolean preview, 
			PageUrlBuilder pageUrlBuilder, 
			ComponentRepository componentRepository) {
		
		this.page = page;
		this.preview = preview;
		this.pageUrlBuilder = pageUrlBuilder;
		this.componentRepository = componentRepository;
	}
	
	public Long getId() {
		return page.getId();
	}
	
	public PageNode getNode() {
		return page.getNode();
	}
	
	public Site getSite() {
		return page.getSite();
	}
	
	public Locale getLocale() {
		return page.getLocale();
	}
	
	public Date getCreationDate() {
		return page.getCreationDate();
	}
	
	public String getPathComponent() {
		return page.getPathComponent();
	}

	public boolean isHidden() {
		return page.isHidden();
	}
	
	public boolean isFolder() {
		return page.isFolder();
	}

	public String getPath() {
		return page.getPath();
	}

	public String getUrl() {
		return pageUrlBuilder.getUrl(page);
	}
	
	public String getFullPath() {
		return page.getFullPath();
	}

	public boolean isWildcard() {
		return page.isWildcard();
	}
	
	public boolean isWildcardInPath() {
		return page.isWildcardInPath();
	}

	public Page getParent() {
		return page.getParentPage();
	}

	public Collection getChildPages() {
		return getVisiblePages(page.getChildPages());
	}

	public List getSiblings() {
		PageNode parentNode = page.getNode().getParent();
		return getVisiblePages(parentNode.getChildPages(page.getSite()));
	}
	
	public Page getPreviousSibling() {
		List siblings = getSiblings();
		int i = siblings.indexOf(page);
		if (i > 0) {
			return (Page) siblings.get(i - 1);
		}
		return null;
	}
	
	public Page getNextSibling() {
		List siblings = getSiblings();
		int i = siblings.indexOf(page);
		if (i < siblings.size() - 1) {
			return (Page) siblings.get(i + 1);
		}
		return null;
	}
	
	public Collection getAncestors() {
		return page.getAncestors();
	}

	public String getHandlerName() {
		return page.getHandlerName();
	}

	public VersionContainer getVersionContainer() {
		return page.getVersionContainer();
	}

	public Map getProperties() {
		if (properties == null) {
			ComponentVersion version = page.getComponentVersion(preview);
			if (version != null) {
				Component component = componentRepository.getComponent(version.getType());
				properties = component.buildModel(version);
			}
			else {
				properties = Collections.EMPTY_MAP;
			}
		}
		return properties;
	}

	public String getTitle() {
		return page.getTitle(preview);
	}
	
	public boolean isPublished() {
		return page.isPublished();
	}

	public boolean isEnabled() {
		return page.isEnabled();
	}

	public boolean isVisible() {
		return page.isVisible(preview);
	}

	private List getVisiblePages(List pages) {
		ArrayList result = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (page.isVisible(preview)) {
				result.add(page);
			}
		}
		return result;
	}
	
	public String toString() {
		return page.toString();
	}

	public boolean equals(Object o) {
		if (o instanceof PageFacade) {
			PageFacade other = (PageFacade) o; 
			return page.equals(other.page) && preview == other.preview;
		}
		return false;
	}

	public int hashCode() {
		return page.hashCode() + (preview ? 1 : 0);
	}
}
