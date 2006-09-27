package org.riotfamily.riot.dao;

/**
 * In order to support cut and paste operations a RiotDao must be capable of
 * performing add and remove operations without saving or deleting. 
 */
public interface CutAndPasteEnabledDao extends RiotDao {

	/**
	 * Removes the entity from the given parent.
	 */
	public void removeChild(Object entity, Object parent);
	
	/**
	 * Adds the entity to a new parent.
	 */
	public void addChild(Object entity, Object parent);
	
}
