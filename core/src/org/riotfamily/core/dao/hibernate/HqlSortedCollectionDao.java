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

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Swapping;
import org.riotfamily.core.screen.list.ListParamsImpl;

public class HqlSortedCollectionDao extends HqlCollectionDao
		implements Swapping {

	private String positionProperty;
	
	public HqlSortedCollectionDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void setPositionProperty(String positionProperty) {
		this.positionProperty = positionProperty;
	}

	public boolean canSwap(Object entity, Object parent, 
			ListParams params, int swapWith) {
		
		List<?> items = listInternal(parent, new ListParamsImpl(params));
		int i = items.indexOf(entity) + swapWith;
		return i >= 0 && i < items.size();
	}
	
	public void swapEntity(Object entity, Object parent, ListParams params, 
			int swapWith) {
		
		List<?> items = listInternal(parent, new ListParamsImpl(params));
		
		int i = items.indexOf(entity);
    	Object nextItem = items.get(i + swapWith);
    	
    	Collection<Object> c = getCollection(parent);
    	
    	c.remove(entity);
    	c.remove(nextItem);
    	
    	Object pos1 = PropertyUtils.getProperty(entity, positionProperty);
    	Object pos2 = PropertyUtils.getProperty(nextItem, positionProperty);
    	
    	PropertyUtils.setProperty(entity, positionProperty, pos2);
    	PropertyUtils.setProperty(nextItem, positionProperty, pos1);
    	
    	c.add(entity);
    	c.add(nextItem);
	}
	
}
