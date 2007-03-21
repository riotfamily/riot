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
	
	private String cssClass;
	
	private List columns;
	
	private List commands;

	private String defaultCommandId;
		
	private boolean lastOnPage;
	
	public ListItem() {
	}
	
	public ListItem(String objectId) {
		this.objectId = objectId;
	}

	public List getColumns() {
		return this.columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
	}

	public String getCssClass() {
		return this.cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public List getCommands() {
		return this.commands;
	}

	public void setCommands(List commands) {
		this.commands = commands;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getRowIndex() {
		return this.rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public boolean isLastOnPage() {
		return this.lastOnPage;
	}

	public void setLastOnPage(boolean lastOnPage) {
		this.lastOnPage = lastOnPage;
	}

	void setDefaultCommandIds(String[] defaultCommandIds) {
		if (defaultCommandIds != null) {
			for (int i = 0; i < defaultCommandIds.length; i++) {
				CommandState state = getCommandState(defaultCommandIds[i]);
				if (state != null && state.isEnabled()) {
					defaultCommandId = state.getId();
					break;
				}
			}
		}
	}
	
	private CommandState getCommandState(String id) {
		Iterator it = commands.iterator();
		while (it.hasNext()) {
			CommandState state = (CommandState) it.next();
			if (state.getId().equals(id)) {
				return state;
			}
		}
		return null;
	}
	
	public void setDefaultCommandId(String defaultCommandId) {
		this.defaultCommandId = defaultCommandId;
	}
	
	public String getDefaultCommandId() {
		return defaultCommandId;
	}
		
}
