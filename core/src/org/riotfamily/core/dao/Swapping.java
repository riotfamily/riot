package org.riotfamily.core.dao;

/**
 * RiotDao that allows to change the position of an entity using the 
 * SwapCommand.
 */
public interface Swapping extends RiotDao {

	public boolean canSwap(Object entity, Object parent, 
    		ListParams params, int swapWith);
	
	public void swapEntity(Object entity, Object parent, 
    		ListParams params, int swapWith);

}
