package org.riotfamily.core.dao;

public interface Constraints extends RiotDao {

	/**
	 * Returns whether a child can be added to the given parent.
	 */
	public boolean canAdd(Object parent);
	
	/**
	 * Returns whether the given entity can be deleted.
	 */
	public boolean canDelete(Object entity);

}
