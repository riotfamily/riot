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
package org.riotfamily.core.dao.hibernate;

import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Swapping;
import org.riotfamily.core.screen.list.ListParamsImpl;

public class HqlIndexedListDao extends HqlCollectionDao 
		implements Swapping {

	private String indexColumn;
	
	public HqlIndexedListDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	protected void initDao() throws Exception {
		super.initDao();
		AbstractCollectionPersister persister = (AbstractCollectionPersister) 
				getSessionFactory().getCollectionMetadata(getRole());

		indexColumn = persister.getIndexColumnNames()[0];
	}
	
	/**
	 * Always returns false, as the list must always be sorted by the index.
	 */
	@Override
	public boolean canSortBy(String property) {
		return false;
	}
	
	@Override
	protected String getOrderBy(ListParams params) {
		return indexColumn;
	}
	
	public boolean canSwap(Object entity, Object parent,
			ListParams params, int swapWith) {
		
		List<?> items = listInternal(parent, new ListParamsImpl(params));
    	int i = items.indexOf(entity) + swapWith;
    	return i >= 0 && i < items.size();
	}

	public void swapEntity(Object entity, Object parent, 
    		ListParams params, int swapWith) {
    	
    	List<?> items = listInternal(parent, new ListParamsImpl(params));
    	int i = items.indexOf(entity);
    	Object nextItem = items.get(i + swapWith);
    	
    	List<?> list = (List<?>) getCollection(parent);
    	Collections.swap(list, list.indexOf(entity), list.indexOf(nextItem));
	}
}
