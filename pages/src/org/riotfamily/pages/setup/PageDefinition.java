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
package org.riotfamily.pages.setup;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;

/**
 * @author flx
 * @since 6.5
 */
public class PageDefinition {

	private String pathComponent;

	private String handlerName;

	private String childHandlerName;

	private List definitions;

	private boolean hidden;

	private boolean published = true;

	private boolean systemNode = true;

	private boolean folder;

	private Properties globalProps;

	private HashMap localizedProps;

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

	public void setChildHandlerName(String childHandlerName) {
		this.childHandlerName = childHandlerName;
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

	public void setGlobalProps(Properties globalProps) {
		this.globalProps = globalProps;
	}

	public void setLocalizedProps(HashMap localizedProps) {
		this.localizedProps = localizedProps;
	}

	public List getDefinitions() {
		return this.definitions;
	}

	public void setDefinitions(List definitions) {
		this.definitions = definitions;
	}

	public PageNode createNode(PageNode parent, List sites, PageDao pageDao) {
		PageNode node = new PageNode();
		node.setParent(parent);
		node.setHandlerName(handlerName);
		node.setSystemNode(systemNode);
		node.setChildHandlerName(childHandlerName);
		node.setHidden(hidden);
		createPages(node, sites, pageDao);
		if (definitions != null) {
			Iterator it = definitions.iterator();
			while (it.hasNext()) {
				PageDefinition childDefinition = (PageDefinition) it.next();
				PageNode childNode = childDefinition.createNode(node, sites, pageDao);
				node.addChildNode(childNode);
			}
		}
		pageDao.saveNode(node);
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
			addPageProps(page, site);
			node.addPage(page);
		}
	}

	private void addPageProps(Page page, Site site) {
		HashMap newProps = new HashMap();
		if (globalProps != null) {
			newProps.putAll(globalProps);
		}
		if (localizedProps != null) {
			Map localizedMap = (Map) localizedProps.get(site.getLocale().toString());
			if(localizedMap != null) {
				newProps.putAll(localizedMap);
			}
		}
		page.getProperties(false).putAll(newProps);
	}


}
