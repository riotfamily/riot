package org.riotfamily.riot.dao;

import java.util.Collection;

import org.springframework.dao.DataAccessException;

/**
 * Interface that provides access to an underlying data store.
 */
public interface RiotDao {
	
	/**
	 * Returns the class that is accessed by the dao.
	 */
	public Class getEntityClass();

	/**
	 * Returns the id of the given entity. Implementors will most likely need
	 * to perform a type conversion in order to return a String representation.
	 * The returned String must be parsable by the {@link #load(String) load()}
	 * method.
	 */
	public String getObjectId(Object entity);
	
	/**
	 * Returns the entity with the given id.
	 */
	public Object load(String id) throws DataAccessException;
	
	/**
	 * Updates the given entity.
	 */
	public void update(Object entity) throws DataAccessException;

	/**
	 * Saves the given entity.
	 */
	public void save(Object entity, Object parent) throws DataAccessException;
	
	/**
	 * Deletes the given entity.
	 */
	public void delete(Object entity, Object parent) throws DataAccessException;
	
	/**
	 * Returns a list of entities.
	 */
	public Collection list(Object parent, ListParams params) throws DataAccessException;
	
	/**
	 * Returns the total number of entities.
	 */
	public int getListSize(Object parent, ListParams params) throws DataAccessException;

}
