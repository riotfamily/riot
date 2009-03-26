package org.riotfamily.core.dao;

public interface TreeDao extends ParentChildDao {

	public boolean isNode(Object object);
	
	public boolean hasChildren(Object parent, Object root, ListParams params);

}
