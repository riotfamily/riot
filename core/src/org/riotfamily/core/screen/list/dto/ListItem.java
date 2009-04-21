/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
