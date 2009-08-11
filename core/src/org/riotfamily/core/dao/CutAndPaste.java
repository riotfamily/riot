package org.riotfamily.core.dao;

/**
 * In order to support cut and paste operations a RiotDao must be capable of
 * performing add and remove operations without saving or deleting. 
 */
public interface CutAndPaste extends Hierarchy {

	public boolean canCut(Object entity);
	
	public void cut(Object entity, Object parent);
	
	public boolean canPasteCut(Object entity, Object target);

	public void pasteCut(Object entity, Object parent);
	
}
