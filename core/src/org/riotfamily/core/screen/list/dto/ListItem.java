/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		if (children != null) {
			for (ListItem child : children) {
				child.setParentNodeId(objectId);
			}
		}
	}

	@Override
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
	
	@Override
	public int hashCode() {
		return objectId != null ? objectId.hashCode() : 0;
	}
	
	// ----------------------------------------------------------------------
	// Implementation of the ObjectReference interface
	// ----------------------------------------------------------------------
	
	public String getObjectId() {
		return this.objectId;
	}
		
	public String getParentNodeId() {
		return this.parentNodeId;
	}
		
}
