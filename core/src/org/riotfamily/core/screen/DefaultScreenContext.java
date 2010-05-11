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
import org.riotfamily.core.dao.Tree;

public class DefaultScreenContext implements ScreenContext {

	private RiotScreen screen;

	private RiotDao dao;
	
	private HttpServletRequest request;
	
	private String objectId;
	
	private String parentId;
	
	private boolean nestedTreeItem;
	
	private Object object;
	
	private Object parent;
	
	public DefaultScreenContext(RiotScreen screen, HttpServletRequest request,
			String objectId, String parentId, boolean nestedTreeItem) {
		
		this.screen = screen;
		this.request = request;
		this.objectId = objectId;
		this.parentId = parentId;
		this.nestedTreeItem = nestedTreeItem;
		this.dao = ScreenUtils.getDao(screen);
	}
	
	public DefaultScreenContext(RiotScreen screen, Object object, 
			Object parent, boolean nestedTreeItem, 
			ScreenContext other) {
		
		this.screen = screen;
		this.object = object;
		this.parent = parent;
		this.nestedTreeItem = nestedTreeItem;
		this.request = other.getRequest();
		this.dao = ScreenUtils.getDao(screen);
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

	public DefaultScreenContext createParentContext() {
		RiotScreen parentScreen = screen.getParentScreen();
		if (parentScreen == null) {
			return null;
		}
		if (screen instanceof ListScreen) {
			return new DefaultScreenContext(parentScreen, getParent(), null, false, this);
		}
		if (getObject() == null) {
			if (nestedTreeItem) {
				return new DefaultScreenContext(parentScreen, getParent(), null, false, this);		
			}
			return new DefaultScreenContext(parentScreen, null, getParent(), false, this);
		}
		return new DefaultScreenContext(parentScreen, getObject(), null, false, this);
	}
	
	public DefaultScreenContext createNewItemContext(Object parentTreeItem) {
		boolean nested = parentTreeItem != null;
		Object newParent = nested ? parentTreeItem : getParent();
		RiotScreen itemScreen = ScreenUtils.getListScreen(screen).getItemScreen();
		return new DefaultScreenContext(itemScreen, null, newParent, nested, this);
	}
	
	public DefaultScreenContext createItemContext(Object item) {
		RiotScreen itemScreen = ScreenUtils.getListScreen(screen).getItemScreen();
		if (itemScreen instanceof ListScreen) {
			return new DefaultScreenContext(itemScreen, null, item, false, this);
		}
		return new DefaultScreenContext(itemScreen, item, null, false, this);
	}
	
	public DefaultScreenContext createChildContext(RiotScreen screen) {
		if (screen instanceof ListScreen) {
			return new DefaultScreenContext(screen, null, getObject(), false, this);
		}
		return new DefaultScreenContext(screen, getObject(), null, false, this);
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
	
	public Object getParentNode() {
		if (parentId != null) {
			if (nestedTreeItem) {
				return dao.load(parentId);
			}
		}
		else if (getObject() != null) {
			if (dao instanceof Tree) {
				return ((Tree) dao).getParentNode(object);
			}
		}
		return null;
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
		DefaultScreenContext ctx = this;
		while (ctx != null) {
			path.add(0, ctx.getLink());
			ctx = ctx.createParentContext();
		}
		return path;
	}
	
	@Override
	public String toString() {
		return String.format("ScreenContext[screen=%s]", getScreen() != null ? getScreen().getId() : null);
	}
	
}
