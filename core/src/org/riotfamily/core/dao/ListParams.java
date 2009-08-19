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