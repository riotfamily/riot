package org.riotfamily.core.dao;

public interface TreeDao {

	public Object getParentNode(Object node);

	public boolean hasChildren(Object node, Object root, ListParams params);
	
}
