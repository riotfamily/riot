package org.riotfamily.riot.list.ui;

import org.riotfamily.riot.dao.ListParams;

/**
 * Provides display parameters for a list, like sort order and page number.
 */
public interface MutableListParams extends ListParams {
	
	public void setParentId(String parentId);
	
	public void setPageSize(int pageSize);
	
	public void setFilter(Object filter);
	
	public void setOffset(int offset);

	/**
	 * Changes the sort order so that the list will be ordered by the given
	 * property. The previous order will be shifted to the right (from an
	 * SQL point of view) unless the list was already ordered by the given
	 * property in which case only the sort direction will be toggled.
	 */
	public void orderBy(String property, boolean ascending, boolean caseSensitive);

}
