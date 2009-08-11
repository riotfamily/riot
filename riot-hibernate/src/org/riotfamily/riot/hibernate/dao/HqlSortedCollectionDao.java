package org.riotfamily.riot.hibernate.dao;

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
