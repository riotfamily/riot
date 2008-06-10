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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.dao;

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