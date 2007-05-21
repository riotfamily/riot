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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageNode;

/**
 * @author flx
 * @since 6.5
 */
public class PageDefinition {

	private String pathComponent;

	private String handlerName;

	private String childHandlerName;

	private List children;

	private boolean hidden;

	private boolean published = true;

	private boolean systemNode = true;

	private boolean folder;

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

	public void setChildren(List children) {
		this.children = children;
	}

/*
 	public List getChildren() {
		return this.children;
	}
*/

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

	public PageNode createNode(Collection locales) {
		PageNode node = new PageNode();
		node.setHandlerName(handlerName);
		node.setSystemNode(systemNode);
		node.setChildHandlerName(childHandlerName);
		node.setHidden(hidden);
		createPages(node, locales);
		createChildNodes(node, locales);
		return node;
	}

	private void createPages(PageNode node, Collection locales) {
		Iterator it = locales.iterator();
		while (it.hasNext()) {
			Locale locale = (Locale) it.next();
			Page page = new Page(getPathComponent(), locale);
			page.setPublished(published);
			page.setFolder(folder);
			node.addPage(page);
		}
	}

	private void createChildNodes(PageNode node, Collection locales) {
		if (children != null) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				PageDefinition childDefinition = (PageDefinition) it.next();
				PageNode childNode = childDefinition.createNode(locales);
				node.addChildNode(childNode);
			}
		}
	}

}
