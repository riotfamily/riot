package org.riotfamily.riot.hibernate.dao;

import java.util.List;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.SwappableItemDao;

public class HqlIndexedListDao extends HqlCollectionDao 
		implements SwappableItemDao {

	public void swapEntity(Object entity, Object parent, 
    		ListParams params, int swapWith) {
    	
    	List items = listInternal(parent, params);
    	Object nextItem = items.get(swapWith);
    	
    	List list = (List) getCollection(parent);
    	list.remove(entity);
    	int pos = list.indexOf(nextItem);
    	list.add(pos, entity);
    	
    	getSession().update(parent);
	}
}
