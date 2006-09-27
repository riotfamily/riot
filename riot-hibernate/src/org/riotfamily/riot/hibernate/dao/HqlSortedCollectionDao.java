package org.riotfamily.riot.hibernate.dao;

import java.util.Collection;
import java.util.List;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.SwappableItemDao;

public class HqlSortedCollectionDao extends HqlCollectionDao
		implements SwappableItemDao {

	private String positionProperty;
	
	public void setPositionProperty(String positionProperty) {
		this.positionProperty = positionProperty;
	}

	public void swapEntity(Object entity, Object parent, ListParams params, 
			int swapWith) {
		
		List items = listInternal(parent, params);
    	Object nextItem = items.get(swapWith);
    	
    	Collection c = getCollection(parent);
    	c.remove(entity);
    	c.remove(nextItem);
    	
    	Object pos1 = PropertyUtils.getProperty(entity, positionProperty);
    	Object pos2 = PropertyUtils.getProperty(nextItem, positionProperty);
    	
    	PropertyUtils.setProperty(entity, positionProperty, pos2);
    	PropertyUtils.setProperty(nextItem, positionProperty, pos1);
    	
    	c.add(entity);
    	c.add(nextItem);
    	
    	getSession().update(entity);
    	getSession().update(nextItem);
	}

	
}
