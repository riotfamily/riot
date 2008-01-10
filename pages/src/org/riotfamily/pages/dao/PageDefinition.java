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
import java.util.Iterator;
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

	private String handlerName;

	private List pageDefinitions;

	private boolean hidden;

	private boolean published = true;

	private boolean systemNode = true;

	private boolean folder;

	private Map properties;

	public void setPathComponent(String pathComponent) {
		this.pathComponent = pathComponent;
	}

	public String getPathComponent() {
		return pathComponent != null
				? pathComponent
				: FormatUtils.camelToXmlCase(handlerName);
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
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

	public void setProperties(Map properties) {
		this.properties = properties;
	}

	public List getPageDefinitions() {
		return this.pageDefinitions;
	}

	public void setPageDefinitions(List definitions) {
		this.pageDefinitions = definitions;
	}

	public PageNode createNode(PageNode parent, List sites, PageDao pageDao) {
		PageNode node = new PageNode();
		parent.addChildNode(node);
		node.setHandlerName(handlerName);
		node.setSystemNode(systemNode);
		node.setHidden(hidden);
		createPages(node, sites, pageDao);
		pageDao.saveNode(node);
		if (pageDefinitions != null) {
			Iterator it = pageDefinitions.iterator();
			while (it.hasNext()) {
				PageDefinition childDefinition = (PageDefinition) it.next();
				childDefinition.createNode(node, sites, pageDao);
			}
		}
		return node;
	}

	private void createPages(PageNode node, List sites, PageDao pageDao) {
		Iterator it = sites.iterator();
		while (it.hasNext()) {
			Site site = (Site) it.next();
			Page page = new Page(getPathComponent(), site);
			page.setNode(node);
			page.setPublished(published);
			page.setFolder(folder);
			page.setCreationDate(new Date());
			if (site.getMasterSite() == null) {
				page.getPageProperties().getLiveVersion().setValues(properties);
			}
			node.addPage(page);
			pageDao.deleteAlias(page);
		}
	}

}
