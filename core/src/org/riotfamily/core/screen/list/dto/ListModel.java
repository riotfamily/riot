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
