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
import java.util.Map;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ListModel {

	private String editorId;

	private String parentId;
	
	private String parentEditorId;

	private boolean tree;
	
	private boolean instantAction;
	
	private List<ListColumn> columns;

	private List<ListItem> items;

	private List<CommandButton> listCommands;
	
	private List<CommandButton> itemCommands;
	
	private int pages;

	private int pageSize;

	private int currentPage;

	private int itemsTotal;

	private String filterFormHtml;

	private String cssClass;
	
	private Map<String, String> texts;

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
	
	public String getParentEditorId() {
		return parentEditorId;
	}

	public void setParentEditorId(String parentEditorId) {
		this.parentEditorId = parentEditorId;
	}

	public boolean isTree() {
		return tree;
	}

	public void setTree(boolean tree) {
		this.tree = tree;
	}
	
	public boolean isInstantAction() {
		return instantAction;
	}

	public void setInstantAction(boolean instantAction) {
		this.instantAction = instantAction;
	}

	public List<ListColumn> getColumns() {
		return this.columns;
	}

	public void setColumns(List<ListColumn> columns) {
		this.columns = columns;
	}

	public List<ListItem> getItems() {
		return this.items;
	}

	public void setItems(List<ListItem> items) {
		this.items = items;
	}

	public ListItem findItem(String objectId) {
		if (items != null && objectId != null) {
			Iterator<ListItem> it = items.iterator();
			while (it.hasNext()) {
				ListItem item = it.next();
				if (objectId.equals(item.getObjectId())) {
					return item;
				}
			}
		}
		return null;
	}
	
	public List<CommandButton> getListCommands() {
		return this.listCommands;
	}

	public void setListCommands(List<CommandButton> listCommands) {
		this.listCommands = listCommands;
	}
	
	public List<CommandButton> getItemCommands() {
		return this.itemCommands;
	}

	public void setItemCommands(List<CommandButton> itemCommands) {
		this.itemCommands = itemCommands;
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

	public Map<String, String> getTexts() {
		return texts;
	}

	public void setTexts(Map<String, String> texts) {
		this.texts = texts;
	}

}
