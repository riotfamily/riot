/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.screen;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.dao.Hierarchy;
import org.riotfamily.core.dao.RiotDao;

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
		this.request = other.request;
		this.dao = ScreenUtils.getDao(screen);
	}
	
	public void expose() {
		request.setAttribute(REQUEST_ATTR, this);
	}
	
	public String getObjectId() {
		if (objectId == null && object != null && dao != null) {
			objectId = dao.getObjectId(object);
		}
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getParentId() {
		if (parentId == null && getParent() != null) {
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
		if (screen instanceof ListScreen) {
			return new ScreenContext(parentScreen, getParent(), null, false, this);
		}
		if (getObject() == null) {
			if (nestedTreeItem) {
				return new ScreenContext(parentScreen, getParent(), null, false, this);		
			}
			return new ScreenContext(parentScreen, null, getParent(), false, this);
		}
		return new ScreenContext(parentScreen, getObject(), null, false, this);
	}
	
	public ScreenContext createNewItemContext(Object parentTreeItem) {
		boolean nested = parentTreeItem != null;
		Object newParent = nested ? parentTreeItem : getParent();
		RiotScreen itemScreen = ScreenUtils.getListScreen(screen).getItemScreen();
		return new ScreenContext(itemScreen, null, newParent, nested, this);
	}
	
	public ScreenContext createItemContext(Object item) {
		RiotScreen itemScreen = ScreenUtils.getListScreen(screen).getItemScreen();
		if (itemScreen instanceof ListScreen) {
			return new ScreenContext(itemScreen, null, item, false, this);
		}
		return new ScreenContext(itemScreen, item, null, false, this);
	}
	
	public ScreenContext createChildContext(RiotScreen screen) {
		if (screen instanceof ListScreen) {
			return new ScreenContext(screen, null, getObject(), false, this);
		}
		return new ScreenContext(screen, getObject(), null, false, this);
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
				if (dao instanceof Hierarchy) {
					parent = ((Hierarchy) dao).getParent(object);
				}
			}
		}
		return parent;
	}
	
	public String getTitle() {
		return screen.getTitle(this);
	}
	
	public String getUrl() {
		return HandlerUrlUtils.getContextRelativeUrl(request, screen.getId(), this);
	}
	
	public ScreenLink getLink() {
		boolean isNew = object == null && objectId == null && screen instanceof ItemScreen; 
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
	
	@Override
	public String toString() {
		return String.format("ScreenContext[screen=%s]", getScreen().getId());
	}
	
	public static ScreenContext get(HttpServletRequest request) {
		return (ScreenContext) request.getAttribute(REQUEST_ATTR);
	}

}
