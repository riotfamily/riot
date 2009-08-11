package org.riotfamily.core.screen.list.dto;

import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.core.screen.list.command.ObjectReference;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
@DataTransferObject
public class ListItem implements ObjectReference {

	@RemoteProperty
	private int rowIndex;
	
	@RemoteProperty
	private String objectId;
	
	@RemoteProperty
	private String parentNodeId;
	
	@RemoteProperty
	private List<String> columns;
		
	@RemoteProperty
	private boolean expandable;
	
	@RemoteProperty
	private List<ListItem> children;
	
	public ListItem() {
	}
	
	public List<String> getColumns() {
		return this.columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	public void setParentNodeId(String parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public boolean isExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

	public List<ListItem> getChildren() {
		return children;
	}

	public void setChildren(List<ListItem> children) {
		this.children = children;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ObjectReference) {
			ObjectReference other = (ObjectReference) obj;
			return objectId != null && objectId.equals(other.getObjectId()); 
		}
		return false;
	}
	
	public int hashCode() {
		return objectId != null ? objectId.hashCode() : 0;
	}
	
	// ----------------------------------------------------------------------
	// Implementation of the ObjectReference interface
	// ----------------------------------------------------------------------
	
	public String getObjectId() {
		return this.objectId;
	}
	
	public int getRowIndex() {
		return this.rowIndex;
	}
	
	public String getParentNodeId() {
		return this.parentNodeId;
	}
		
}
