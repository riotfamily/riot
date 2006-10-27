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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.support;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.list.ui.MutableListParams;

/**
 * A bean style implementation of the ListModelParams interface.
 */
public class ListParamsImpl implements MutableListParams, Serializable {

	private String parentId;
	
	private Object filter;

	private List order;
	
	private int pageSize;
	
	private int offset;

	
	public ListParamsImpl() {
	}

	public ListParamsImpl(ListParams params) {
		this.parentId = params.getParentId();
		this.filter = params.getFilter();
		this.order = params.getOrder();
		this.pageSize = params.getPageSize();
		this.offset = params.getOffset();
	}
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Object getFilter() {
		return filter;
	}

	public void setFilter(Object filter) {
		this.filter = filter;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getPage() {
		return offset / pageSize + 1;
	}
	
	public void setPage(int page) {
		offset = (page - 1) * pageSize;
	}

	public List getOrder() {
		return order;
	}

	public void setOrder(List order) {
		this.order = order;
	}
	
	public boolean hasOrder() {
		return order != null && !order.isEmpty();
	}
	
	private Order getPrimaryOrder() {
		if (!hasOrder()) {
			return null;
		}
		return (Order) getOrder().get(0);
	}

	/**
	 * Changes the sort order so that the list will be ordered by the given
	 * property. The previous order will be shifted to the right (from an
	 * SQL point of view) unless the list was already ordered by the given
	 * property in which case only the sort direction will be toggled.
	 */
	public void orderBy(String property, boolean ascending, boolean caseSensitive) {
		if (getOrder() == null) {
			setOrder(new LinkedList());
		}
		else {
			Order primary = getPrimaryOrder();
			if (primary != null && primary.isProperty(property)) {
				primary.toggleDirection();
				return;
			}
			Iterator it = getOrder().iterator();
			while (it.hasNext()) {
				Order order = (Order) it.next();
				if (order.isProperty(property)) {
					it.remove();
				}
			}
		}
		getOrder().add(0, new Order(property, ascending, caseSensitive));
	}
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
    /**
     * Checks if the filter is serializable and sets it to <code>null</code>
     * if not. Afterwards <code>out.defaultWriteObject()</code> is called.
     * <p>
     * The idea is not to force filter implementations to be serializable
     * but to support serialization if needed.
     * </p>
     */ 
	private void writeObject(java.io.ObjectOutputStream out)
    		throws IOException {
		
		if (!(filter instanceof Serializable)) {
			filter = null;
		}
		out.defaultWriteObject();
	}

}
