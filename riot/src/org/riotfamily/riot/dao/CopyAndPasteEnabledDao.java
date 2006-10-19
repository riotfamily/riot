package org.riotfamily.riot.dao;

/**
 * In order to support copy and paste operations a RiotDao must be capable of
 * creating copies of entities. 
 */
public interface CopyAndPasteEnabledDao extends RiotDao {

	/**
	 * Adds a copy of the given entity to the specified parent.
	 */
	public void addCopy(Object entity, Object parent);
	
}
