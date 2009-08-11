package org.riotfamily.riot.hibernate.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.SessionFactory;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Swapping;
import org.riotfamily.core.screen.list.ListParamsImpl;

public class HqlIndexedListDao extends HqlCollectionDao 
		implements Swapping {

	public HqlIndexedListDao(SessionFactory sessionFactory) {
		super(sessionFactory);
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
