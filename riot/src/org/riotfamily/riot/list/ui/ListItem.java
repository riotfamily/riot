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
package org.riotfamily.riot.list.ui;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.riot.list.command.CommandState;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ListItem {

	private int rowIndex;
	
	private String objectId;
	
	private String parentId;
	
	private String parentEditorId;
	
	private String cssClass;
	
	private List<String> columns;
	
	private List<CommandState> commands;

	private CommandState defaultCommand;
		
	private boolean expandable;
	
	private ListModel children;
	
	public ListItem() {
	}
	
	public ListItem(String objectId, String parentId, String parentEditorId) {
		this.objectId = objectId;
		this.parentId = parentId;
		this.parentEditorId = parentEditorId;
	}

	public List<String> getColumns() {
		return this.columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public String getCssClass() {
		return this.cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public List<CommandState> getCommands() {
		return this.commands;
	}

	public void setCommands(List<CommandState> commands) {
		this.commands = commands;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getParentEditorId() {
		return parentEditorId;
	}

	public void setParentEditorId(String parentEditorId) {
		this.parentEditorId = parentEditorId;
	}

	public int getRowIndex() {
		return this.rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public boolean isExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

	public ListModel getChildren() {
		return children;
	}

	public void setChildren(ListModel children) {
		this.children = children;
	}

	void setDefaultCommandIds(String[] defaultCommandIds) {
		if (defaultCommandIds != null) {
			for (int i = 0; i < defaultCommandIds.length; i++) {
				CommandState state = getCommandState(defaultCommandIds[i]);
				if (state != null && state.isEnabled()) {
					defaultCommand = state;
					break;
				}
			}
		}
	}
	
	private CommandState getCommandState(String id) {
		Iterator<CommandState> it = commands.iterator();
		while (it.hasNext()) {
			CommandState state = it.next();
			if (state.getId().equals(id)) {
				return state;
			}
		}
		return null;
	}
	
	public void setDefaultCommand(CommandState defaultCommand) {
		this.defaultCommand = defaultCommand;
	}
	
	public CommandState getDefaultCommand() {
		return defaultCommand;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ListItem) {
			ListItem other = (ListItem) obj;
			return objectId != null && objectId.equals(other.objectId); 
		}
		return false;
	}
	
	public int hashCode() {
		return objectId != null ? objectId.hashCode() : 0;
	}
		
}
