package org.riotfamily.core.screen.list;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Order;

/**
 * A bean style implementation of the ListModelParams interface.
 */
public class ListParamsImpl implements ListParams, Serializable {

	private Object filter;
	
	private String[] filteredProperties;

	private String search;
	
	private String[] searchProperties;
	
	private List<Order> order;
	
	private int pageSize;
	
	private int offset;

	
	public ListParamsImpl() {
	}
	
	public ListParamsImpl(ListParams params) {
		filter = params.getFilter();
		filteredProperties = params.getFilteredProperties();
		search = params.getSearch();
		searchProperties = params.getSearchProperties();
		order = params.getOrder();
	}
	
	public void adjust(int itemsTotal) {
		if (itemsTotal < 0) {
			pageSize = -1;
		}
		if (offset >= itemsTotal) {
			setPage(getPage() - 1);
		}		
	}
	
	public Object getFilter() {
		return filter;
	}

	public void setFilter(Object filter) {
		this.filter = filter;
	}
	
	public String[] getFilteredProperties() {
		return this.filteredProperties;
	}

	public void setFilteredProperties(String[] filterProperties) {
		this.filteredProperties = filterProperties;
	}

	public String getSearch() {
		return this.search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
	public String[] getSearchProperties() {
		return this.searchProperties;
	}

	public void setSearchProperties(String[] searchProperties) {
		this.searchProperties = searchProperties;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset >= 0 ? offset : 0;
	}
	
	public int getPage() {
		if (pageSize <= 0) {
			return 0;
		}
		return offset / pageSize + 1;
	}
	
	public void setPage(int page) {
		setOffset((page - 1) * pageSize);
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}
	
	public void setOrder(Order order) {
		this.order = new LinkedList<Order>();
		if (order != null) {
			this.order.add(order);
		}
	}
	
	public boolean hasOrder() {
		return order != null && !order.isEmpty();
	}
	
	public Order getPrimaryOrder() {
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
			setOrder(new LinkedList<Order>());
		}
		else {
			Order primary = getPrimaryOrder();
			if (primary != null && primary.isProperty(property)) {
				primary.toggleDirection();
				return;
			}
			Iterator<Order> it = getOrder().iterator();
			while (it.hasNext()) {
				Order order = it.next();
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

}
