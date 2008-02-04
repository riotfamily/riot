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

import java.util.List;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ListModel {

	private String editorId;

	private String parentId;

	private List columns;

	private List items;

	private List listCommands;
	
	private List batchCommands;

	private int itemCommandCount;

	private int pages;

	private int pageSize;

	private int currentPage;

	private int itemsTotal;

	private String filterFormHtml;

	private String cssClass;

	public ListModel(int itemsTotal, int pageSize, int currentPage) {
		this.itemsTotal = itemsTotal;
		this.pageSize = pageSize;
		this.currentPage = currentPage;
		if (pageSize > 0) {
			pages = (int) itemsTotal / pageSize + 1;
			if (itemsTotal % pageSize == 0) {
				pages--;
			}
		}
	}

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

	public List getItems() {
		return this.items;
	}

	public void setItems(List items) {
		this.items = items;
	}

	public List getListCommands() {
		return this.listCommands;
	}

	public void setListCommands(List listCommands) {
		this.listCommands = listCommands;
	}
	
	public List getBatchCommands() {
		return this.batchCommands;
	}

	public void setBatchCommands(List batchCommands) {
		this.batchCommands = batchCommands;
	}

	public int getItemCommandCount() {
		return this.itemCommandCount;
	}

	public void setItemCommandCount(int itemCommandCount) {
		this.itemCommandCount = itemCommandCount;
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public int getItemsTotal() {
		return this.itemsTotal;
	}

	public int getPages() {
		return this.pages;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public String getFilterFormHtml() {
		return this.filterFormHtml;
	}

	public void setFilterFormHtml(String filterFormHtml) {
		this.filterFormHtml = filterFormHtml;
	}

	public String getCssClass() {
		return this.cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

}
