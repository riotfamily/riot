package org.riotfamily.core.dao;

import java.util.List;

public interface ListParams {

	/**
	 * Returns an object populated by the list's filter-form, 
	 * or <code>null</code> if the list does not have a filter.
	 */
	public Object getFilter();

	/**
	 * Returns the names of all properties bound to the filter-form,
	 * or <code>null</code> if the list does not have a filter.
	 * @since 6.4
	 */
	public String[] getFilteredProperties();
	
	/**
	 * Returns the String that should be used to perform a full-text search,
	 * or <code>null</code> if no search should be performed.
	 * @since 6.4
	 */
	public String getSearch();
	
	/**
	 * Returns the name of all properties that should be included in the
	 * full-text search, or <code>null</code> if searching is disabled.
	 * @since 6.4
	 */
	public String[] getSearchProperties();
	
	/**
	 * Returns whether sort-order was specified.
	 */
	public boolean hasOrder();

	/**
	 * Returns a list of {@link Order} objects that should be used to sort
	 * the list.
	 */
	public List<Order> getOrder();

	/**
	 * Returns the maximum number of objects that should be displayed on
	 * a page. 
	 */
	public int getPageSize();

	/**
	 * Returns the offset (starting at 0) of the first object that should be 
	 * displayed.
	 */
	public int getOffset();

}