package org.riotfamily.riot.dao;

import org.riotfamily.riot.list.command.core.SwapCommand;

/**
 * RiotDao that allows to change the position of an entity using the 
 * {@link SwapCommand swap command}.
 */
public interface SwappableItemDao extends RiotDao {

	public void swapEntity(Object entity, Object parent, 
    		ListParams params, int swapWith);

}
