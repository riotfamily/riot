package org.riotfamily.riot.dao;

public interface TreeHintDao extends ParentChildDao {

	public boolean hasChildren(Object parent, Object root, ListParams params);

}
