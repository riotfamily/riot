package org.riotfamily.core.dao;

public interface TreeDao extends RiotDao {

	/**
	 * Returns the parent node of the given object. The method must return
	 * <code>null</code> for the root node(s).
	 */
	public Object getParentNode(Object node);

	/**
	 * Returns whether the given node has any children, i.e. can be expanded.
	 * Note that the <code>parent</code> argument is <em>not</em> the parent 
	 * of the given item, but the parent of the whole tree. Hence the argument
	 * will be <code>null</code> unless the tree is nested within another list.
	 */
	public boolean hasChildren(Object node, Object parent, ListParams params);
	
}
