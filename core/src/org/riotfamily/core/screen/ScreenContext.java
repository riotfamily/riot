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
package org.riotfamily.core.screen;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.mapping.HandlerUrlUtils;
import org.riotfamily.core.dao.ParentChildDao;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.form.FormScreen;

public class ScreenContext {

	private static final String REQUEST_ATTR = "context";
	
	private RiotScreen screen;

	private RiotDao dao;
	
	private HttpServletRequest request;
	
	private String objectId;
	
	private String parentId;
	
	private boolean nestedTreeItem;
	
	private Object object;
	
	private Object parent;
	
	public ScreenContext(RiotScreen screen, HttpServletRequest request,
			String objectId, String parentId, boolean nestedTreeItem) {
		
		this.screen = screen;
		this.request = request;
		this.objectId = objectId;
		this.parentId = parentId;
		this.nestedTreeItem = nestedTreeItem;
		this.dao = ScreenUtils.getDao(screen);
	}
	
	public ScreenContext(RiotScreen screen, Object object, 
			Object parent, boolean nestedTreeItem, 
			ScreenContext other) {
		
		this.screen = screen;
		this.object = object;
		this.parent = parent;
		this.nestedTreeItem = nestedTreeItem;
		this.dao = ScreenUtils.getDao(screen);
		this.request = other.request;
	}
	
	public void expose() {
		request.setAttribute(REQUEST_ATTR, this);
	}
	
	public static ScreenContext get(HttpServletRequest request) {
		return (ScreenContext) request.getAttribute(REQUEST_ATTR);
	}
	
	public String getObjectId() {
		if (objectId == null && object != null) {
			objectId = dao.getObjectId(object);
		}
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getParentId() {
		if (parentId == null && parent != null) {
			if (nestedTreeItem) {
				parentId = dao.getObjectId(parent);
			}
			else {
				ListScreen parentList = ScreenUtils.getParentListScreen(screen);
				if (parentList != null) {
					parentId = parentList.getDao().getObjectId(parent);
				}
			}
		}
		return parentId;
	}

	public boolean isNestedTreeItem() {
		return nestedTreeItem;
	}

	public RiotScreen getScreen() {
		return screen;
	}
	
	public RiotDao getDao() {
		return dao;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public ScreenContext createParentContext() {
		RiotScreen parentScreen = screen.getParentScreen();
		if (parentScreen == null) {
			return null;
		}
		Object parentObject = object;
		if (parentScreen instanceof ListScreen) {
			parentObject = getParent();
		}
		return new ScreenContext(parentScreen, null, parentObject, false, this);
	}
	
	public ScreenContext createNewItemContext(Object parentTreeItem) {
		boolean nested = parentTreeItem != null;
		Object newParent = nested ? parentTreeItem : getObject();
		RiotScreen itemScreen = ScreenUtils.getListScreen(screen).getItemScreen();
		return new ScreenContext(itemScreen, null, newParent, nested, this);
	}
	
	public ScreenContext createItemContext(Object item) {
		RiotScreen itemScreen = ScreenUtils.getListScreen(screen).getItemScreen();
		return new ScreenContext(itemScreen, item, null, false, this);
	}
	
	public String getListStateKey() {
		return ScreenUtils.getListScreen(getScreen()).getListStateKey(this);
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	public Object getObject() {
		if (object == null && objectId != null) {
			object = dao.load(objectId);
		}
		return object;
	}
	
	public Object getParent() {
		if (parent == null) {
			if (parentId != null) {
				if (nestedTreeItem) {
					parent = dao.load(parentId);
				}
				else {
					ListScreen parentList = ScreenUtils.getParentListScreen(screen);
					parent = parentList.getDao().load(parentId);
				}
			}
			else if (getObject() != null) {
				if (dao instanceof ParentChildDao) {
					parent = ((ParentChildDao) dao).getParent(object);
				}
			}
		}
		return parent;
	}
	
	public String getTitle() {
		return screen.getTitle(getObject());
	}
	
	public String getUrl() {
		return HandlerUrlUtils.getUrl(request, screen.getId(), this);
	}
	
	public ScreenLink getLink() {
		//TODO Support isNew for other screen types too
		boolean isNew = objectId == null && screen instanceof FormScreen; 
		return new ScreenLink(getTitle(), getUrl(), screen.getIcon(), isNew);
	}
	
	public List<ScreenLink> getPath() {
		List<ScreenLink> path = Generics.newArrayList();
		ScreenContext ctx = this;
		while (ctx != null) {
			path.add(0, ctx.getLink());
			ctx = ctx.createParentContext();
		}
		return path;
	}

}
