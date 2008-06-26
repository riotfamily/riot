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
package org.riotfamily.pages.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageDefinition {

	private String pathComponent;

	private String type;

	private List<PageDefinition> pageDefinitions;

	private boolean hidden;

	private boolean published = true;

	private boolean systemNode = true;

	private boolean folder;

	private Map<String, Object> properties;

	public void setPathComponent(String pathComponent) {
		this.pathComponent = pathComponent;
	}

	public String getPathComponent() {
		return pathComponent != null
				? pathComponent
				: FormatUtils.camelToXmlCase(type);
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public void setSystemNode(boolean systemNode) {
		this.systemNode = systemNode;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public List<PageDefinition> getPageDefinitions() {
		return this.pageDefinitions;
	}

	public void setPageDefinitions(List<PageDefinition> definitions) {
		this.pageDefinitions = definitions;
	}

	public PageNode createNode(PageNode parent, List<Site> sites, PageDao pageDao) {
		PageNode node = new PageNode();
		parent.addChildNode(node);
		node.setPageType(type);
		node.setSystemNode(systemNode);
		node.setHidden(hidden);
		createPages(node, sites, pageDao);
		pageDao.saveNode(node);
		for (Page page : node.getPages()) {
			pageDao.publishPageProperties(page);
		}
		if (pageDefinitions != null) {
			for (PageDefinition childDefinition : pageDefinitions) {
				childDefinition.createNode(node, sites, pageDao);
			}
		}
		return node;
	}

	private void createPages(PageNode node, List<Site> sites, PageDao pageDao) {
		for (Site site : sites) {
			Page page = new Page(getPathComponent(), site);
			page.setNode(node);
			page.setPublished(published);
			page.setFolder(folder);
			page.setCreationDate(new Date());
			if (site.getMasterSite() == null) {
				page.getPageProperties().getPreviewVersion().wrap(properties);
			}
			node.addPage(page);
			pageDao.deleteAlias(page);
		}
	}

}
