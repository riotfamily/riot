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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.ui;

import java.util.List;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * @since 6.4
 */
public class ListTable {

	private String editorId;
	
	private String parentId;
	
	private List columns;
	
	private List rows;
	
	private List listCommands;

	private int itemCommandCount;
	
	public String getEditorId() {
		return this.editorId;
	}

	public void setEditorId(String editorId) {
		this.editorId = editorId;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public List getColumns() {
		return this.columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
	}

	public List getRows() {
		return this.rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	public List getListCommands() {
		return this.listCommands;
	}

	public void setListCommands(List listCommands) {
		this.listCommands = listCommands;
	}

	public int getItemCommandCount() {
		return this.itemCommandCount;
	}

	public void setItemCommandCount(int itemCommandCount) {
		this.itemCommandCount = itemCommandCount;
	}
	
}
