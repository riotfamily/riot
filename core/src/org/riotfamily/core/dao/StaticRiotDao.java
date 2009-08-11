package org.riotfamily.core.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class StaticRiotDao extends RiotDaoAdapter {

	private List<?> items;
	
	private Class<?> entityClass;
	
	public StaticRiotDao(List<?> items) {
		Assert.notEmpty(items);
		this.items = items;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
	public Class<?> getEntityClass() {
		if (entityClass == null) {
			entityClass = items.get(0).getClass(); 
		}
		return entityClass;
	}
	
	public Collection<?> list(Object parent, ListParams params) {
		return items;
	}
	
	public String getObjectId(Object entity) {
		int index = items.indexOf(entity);
		Assert.isTrue(index != -1, "Item not found in list. Make sure " +
				"hashCode() and equals() are properly implemented.");
		
		return String.valueOf(index); 
	}
	
	public Object load(String id) throws DataAccessException {
		int index = Integer.parseInt(id);
		return items.get(index);
	}
}
