package org.riotfamily.core.dao;

/**
 * In order to support copy and paste operations a RiotDao must be capable of
 * creating copies of entities. 
 */
public interface CopyAndPaste extends RiotDao {

	public boolean canCopy(Object entity);
	
	public boolean canPasteCopy(Object entity, Object dest);
	
	public void pasteCopy(Object entity, Object dest);
	
}
