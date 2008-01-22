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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.riotfamily.common.beans.MapWrapper;
import org.riotfamily.common.web.mapping.AttributePattern;
import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.components.locator.DefaultSlotResolver;
import org.riotfamily.components.locator.SlotResolver;
import org.riotfamily.components.model.ComponentListLocation;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageHandlerMapping;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;

/**
 * ComponentListLocator that uses the page's node-id as component-path.
 * You can use this implementation for contents that should be the same
 * on all sites.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PageNodeComponentListLocator {

	public static final String TYPE = "pageNode";

	public static final String TYPE_PREFIX = "pageNode-";

	private PageDao pageDao;

	private PathCompleter pathCompleter;

	private SlotResolver slotResolver = new DefaultSlotResolver();

	public PageNodeComponentListLocator(PageDao pageDao,
			PathCompleter pathCompleter) {

		this.pageDao = pageDao;
		this.pathCompleter = pathCompleter;
	}

	public void setSlotResolver(SlotResolver slotResolver) {
		this.slotResolver = slotResolver;
	}

	public boolean supports(String type) {
		return type.equals(TYPE) || type.startsWith(TYPE_PREFIX);
	}

	public ComponentListLocation getLocation(HttpServletRequest request) {
		ComponentListLocation location = new ComponentListLocation();
		Page page = PageHandlerMapping.getPage(request);
		if (page.isWildcardInPath()) {
			Map attributes = PageHandlerMapping.getWildcardAttributes(request);
			String jsonString = JSONObject.fromObject(attributes).toString();
			location.setType(TYPE_PREFIX + page.getId());
			location.setPath(jsonString);
		}
		else {
			location.setType(TYPE);
			location.setPath(page.getNode().getId().toString());
		}
		location.setSlot(slotResolver.getSlot(request));
		return location;
	}

	public ComponentListLocation getParentLocation(ComponentListLocation location) {
		PageNode pageNode = loadPageNode(location);
		ComponentListLocation parentLocation = new ComponentListLocation(location);
		location.setType(TYPE);
		location.setPath(pageNode.getParent().getId().toString());
		return parentLocation;
	}

	public String getUrl(ComponentListLocation location) {
		PageNode pageNode = loadPageNode(location);
		Page page = pageNode.getPage(pageDao.getDefaultSite());
		String url = page.getUrl(pathCompleter);
		if (page.isWildcardInPath()) {
			Map attributes = JSONObject.fromObject(location.getPath());
			url = new AttributePattern(url).fillInAttributes(new MapWrapper(attributes));
		}
		return url;
	}

	private PageNode loadPageNode(ComponentListLocation location) {
		String id;
		if (location.getType().equals(TYPE)) {
			 id = location.getPath();
		}
		else {
			String type = location.getType();
			int i = type.indexOf('-');
			id = type.substring(i + 1);
		}
		return pageDao.loadPageNode(new Long(id));
	}
}
