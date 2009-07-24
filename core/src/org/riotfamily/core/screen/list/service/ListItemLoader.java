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
package org.riotfamily.core.screen.list.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.beans.ProtectedBeanWrapper;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.RootNodeTreeDao;
import org.riotfamily.core.dao.TreeDao;
import org.riotfamily.core.screen.list.ColumnConfig;
import org.riotfamily.core.screen.list.ListRenderContext;
import org.riotfamily.core.screen.list.dto.ListItem;
import org.springframework.beans.NullValueInNestedPathException;

/**
 * List service handler that handles the loading of list items. 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
class ListItemLoader extends ChooserCommandHandler implements ListRenderContext {
	
	protected RootNodeTreeDao rootNodeTreeDao;
	
	ListItemLoader(ListService service, String key, 
			HttpServletRequest request) {
		
		super(service, key, request);
		if (dao instanceof RootNodeTreeDao) {
			rootNodeTreeDao = (RootNodeTreeDao) dao;
		}
	}
	
	public List<ListItem> getItems(String parentId) {
		Object[] expanded;
		if (parentId != null) {
			expanded = new Object[] {dao.load(parentId)};
		}
		else {
			expanded = loadExpanded(null);
		}
		return createItems(expanded, 0, parentId);
	}
	
	protected List<ListItem> createItems(Object[] expanded, int i, String parentNodeId) {
		Collection<?> objects;
		if (i == 0 && rootNodeTreeDao != null && expanded.length == 2) {
			objects = Collections.singletonList(expanded[1]);
		}
		else {
			objects = dao.list(expanded[i], state.getParams());
		}
		ArrayList<ListItem> items = Generics.newArrayList(objects.size());
		Object next = i + 1 < expanded.length ? expanded[i + 1] : null;
		for (Object object : objects) {
			ListItem item = new ListItem();
			String objectId = dao.getObjectId(object);
			item.setObjectId(objectId);
			item.setParentNodeId(parentNodeId);
			item.setColumns(getColumns(object));
			item.setExpandable(isExpandable(object));
			if (object.equals(next)) {
				item.setChildren(createItems(expanded, i + 1, objectId));
			}
			item.setRowIndex(items.size());
			items.add(item);
		}
		return items;
	}
	
	/**
	 * Returns a List of HTML markup for each column.
	 */
	private List<String> getColumns(Object object) {
		ArrayList<String> result = Generics.newArrayList();
		ProtectedBeanWrapper wrapper = new ProtectedBeanWrapper(object);
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
	
	protected Object[] loadExpanded(String expandedId) {
		List<Object> result = Generics.newArrayList();
		if (expandedId != null) {
			Object expanded = dao.load(expandedId);
			while (expanded != null) {
				result.add(0, expanded);
				expanded = ((TreeDao) dao).getParentNode(expanded);
			}
		}
		else if (rootNodeTreeDao != null) {
			result.add(0, rootNodeTreeDao.getRootNode(getParent()));
		}
		result.add(0, getParent());
		return result.toArray();
	}
	
	private boolean isExpandable(Object node) {
		if (dao instanceof TreeDao) {
			return ((TreeDao) dao).hasChildren(node, getParent(), state.getParams());
		}
		return false;
	}
	
	// ------------------------------------------------------------------------
	// Implementation of the RenderContext interface
	// ------------------------------------------------------------------------
	
	public String getContextPath() {
		return request.getContextPath();
	}
	
	public MessageResolver getMessageResolver() {
		return messageResolver;
	}

}
