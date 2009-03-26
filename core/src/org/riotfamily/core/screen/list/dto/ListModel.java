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
 * Portions created by the Initial Developer are Copyright (C) 2007
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
import org.riotfamily.core.screen.list.ListParamsImpl;

@DataTransferObject
public class ListModel {

	@RemoteProperty
	private int itemsTotal;
	
	@RemoteProperty
	private int pageSize;

	@RemoteProperty
	private int currentPage;
	
	@RemoteProperty
	private int pages;

	@RemoteProperty
	private List<ListColumn> columns;
	
	@RemoteProperty
	private List<ListItem> items;
	
	@RemoteProperty
	private List<CommandButton> commandButtons;
	
	@RemoteProperty
	private boolean tree;
	
	@RemoteProperty
	private String filterFormHtml;

	public ListModel(List<ListItem> items, int itemsTotal, ListParamsImpl params) {
		this.items = items;
		this.itemsTotal = Math.max(itemsTotal, items.size());
		this.currentPage = params.getPage();
		this.pageSize = params.getPageSize();
		if (pageSize > 0) {
			pages = (int) itemsTotal / pageSize + 1;
			if (itemsTotal % pageSize == 0) {
				pages--;
			}
		}
	}

	public int getItemsTotal() {
		return itemsTotal;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPages() {
		return pages;
	}

	public List<ListColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ListColumn> columns) {
		this.columns = columns;
	}

	public List<ListItem> getItems() {
		return items;
	}

	public void setItems(List<ListItem> items) {
		this.items = items;
	}

	public boolean isTree() {
		return tree;
	}
	
	public void setTree(boolean tree) {
		this.tree = tree;
	}
	
	public List<CommandButton> getCommandButtons() {
		return commandButtons;
	}

	public void setCommandButtons(List<CommandButton> commandButtons) {
		this.commandButtons = commandButtons;
	}

	public String getFilterFormHtml() {
		return filterFormHtml;
	}
	
	public void setFilterFormHtml(String filterFormHtml) {
		this.filterFormHtml = filterFormHtml;
	}

}
