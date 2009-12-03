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
package org.riotfamily.core.screen.list.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.SingleRoot;
import org.riotfamily.core.dao.Tree;
import org.riotfamily.core.screen.list.ColumnConfig;
import org.riotfamily.core.screen.list.ListRenderContext;
import org.riotfamily.core.screen.list.dto.ListItem;
import org.riotfamily.core.security.AccessController;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NullValueInNestedPathException;

/**
 * List service handler that handles the loading of list items. 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
class ListItemLoader extends ChooserCommandHandler implements ListRenderContext {
	
	protected Tree tree;
	
	protected SingleRoot rootNodeTreeDao;
	
	ListItemLoader(ListService service, String key, 
			HttpServletRequest request) {
		
		super(service, key, request);
		if (dao instanceof Tree) {
			tree = (Tree) dao;
			if (dao instanceof SingleRoot) {
				rootNodeTreeDao = (SingleRoot) dao;
			}
		}
	}
	
	protected List<ListItem> createItems(String expandedId) {
		ListItem expanded = null;
		Object parent = loadExpandedParent(expandedId);
		if (parent != null) {
			while (parent != null) {
				List<ListItem> children = createChildItems(parent, expanded);
				expanded = createItem(parent, null);
				expanded.setChildren(children);
				parent = tree.getParentNode(parent);
			}
		}
		if (rootNodeTreeDao != null) {
			return Collections.singletonList(expanded);
		}
		List<ListItem> items = createChildItems(getParent(), expanded);
		return items;
	}
	
	private Object loadExpandedParent(String expandedId) {
		if (tree != null && expandedId != null) {
			Object object = dao.load(expandedId);
			return tree.getParentNode(object);
		}
		if (rootNodeTreeDao != null) {
			return rootNodeTreeDao.getRootNode(getParent());
		}
		return null;
	}
	
	protected List<ListItem> getChildren(String parentId) {
		Object parent = dao.load(parentId);
		List<ListItem> children = createChildItems(parent, null);
		createItem(parent, null).setChildren(children); // Initialize parentNodeId
		return children;
	}

	private List<ListItem> createChildItems(Object parent, ListItem expanded) {
		List<ListItem> items = Generics.newArrayList();
		for (Object child : dao.list(parent, getParams())) {
			if (AccessController.isGranted("viewItem", child, screenContext)) {
				items.add(createItem(child, expanded));
			}
		}
		return items;
	}
	
	private ListItem createItem(Object object, ListItem expanded) {
		String id = dao.getObjectId(object);
		if (expanded != null && id.equals(expanded.getObjectId())) {
			return expanded;
		}
		ListItem item = new ListItem();
		item.setObjectId(id);
		item.setColumns(getColumns(object));
		item.setExpandable(isExpandable(object));
		return item;
	}
	
	/**
	 * Returns a List of HTML markup for each column.
	 */
	private List<String> getColumns(Object object) {
		ArrayList<String> result = Generics.newArrayList();
		BeanWrapper wrapper = new BeanWrapperImpl(object);
		for (ColumnConfig col : screen.getColumns()) {
			String propertyName = col.getProperty();
			Object value = null;
			if (propertyName != null) {
				try {
					value = wrapper.getPropertyValue(propertyName);
				}
				catch (NullValueInNestedPathException ex) {
				}
			}
			else {
				value = object;
			}
			StringWriter writer = new StringWriter();
			service.getRenderer(col).render(value, this, new PrintWriter(writer));
			result.add(writer.toString());
		}
		return result;
	}
		
	private boolean isExpandable(Object node) {
		if (dao instanceof Tree) {
			return ((Tree) dao).hasChildren(node, getParent(), state.getParams());
		}
		return false;
	}
	
	// ------------------------------------------------------------------------
	// Implementation of the RenderContext interface
	// ------------------------------------------------------------------------
	
	public String getContextPath() {
		return request.getContextPath();
	}
	
	@Override
	public MessageResolver getMessageResolver() {
		return messageResolver;
	}

}
