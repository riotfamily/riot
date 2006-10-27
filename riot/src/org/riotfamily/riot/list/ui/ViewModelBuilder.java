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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.view.Pager;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.support.AbstractCommandContext;
import org.riotfamily.riot.list.ui.render.CellRenderer;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Class to create a view model for a list. Since it implements 
 * {@link org.riotfamily.riot.list.ui.render.RenderContext RenderContext} and
 * {@link org.riotfamily.riot.list.command.CommandContext CommandContext} 
 * instances may not be shared across multiple threads and are intended for 
 * one-time use only.
 */
public class ViewModelBuilder extends AbstractCommandContext 
		implements RenderContext {
		
	private Collection items;
	
	private int itemsTotal;
	
	private Object item;
	
	private int index;
	
	private String objectId;
	
	private ColumnConfig columnConfig;
	
	private Object value;
	
	private Command command;
	
	private String rowStyle;
	
	private Locale locale;
	
	public ViewModelBuilder(ListContext context) {
		super(context);
		Object parent = EditorDefinitionUtils.loadParent(
				context.getListDefinition(), context.getParams().getParentId());
		
		items = getDao().list(parent, getParams());
		itemsTotal = Math.max(items.size(), getDao().getListSize(parent, getParams()));
		locale = RequestContextUtils.getLocale(context.getRequest());
	}
	
	public String getDefaultCommandId() {
		return getListConfig().getDefaultCommandId();
	}

	public ViewModel buildModel() {
		Collection headings = buildHeadings();
		Collection rows = buildRows();
		Collection commands = buildCommands();
		Pager pager = buildPager();
		
		return new ViewModel(getListDefinition().getId(), 
				getParentId(), headings, rows, commands, getDefaultCommandId(), 
				pager, getListConfig().getId());
	}
	
	protected Collection buildHeadings() {
		LinkedList headings = new LinkedList();
		Iterator it = getListConfig().getColumnConfigs().iterator();
		while (it.hasNext()) {
			headings.add(buildHeading((ColumnConfig) it.next()));
		}
		return headings;
	}
	
	protected final ListCell buildHeading(ColumnConfig columnConfig) {
		this.columnConfig = columnConfig;
		command = columnConfig.getCommand();
		StringWriter writer = new StringWriter();
		CellRenderer renderer = columnConfig.getHeadingRenderer();
		renderer.render(this, new PrintWriter(writer));
		return new ListCell(getCssClass(), writer.toString());
	}
	
	protected String getCssClass() {
		StringBuffer css = new StringBuffer();
		if (command != null) {
			css.append("command");
		}
		else {
			css.append("data col-");
			css.append(columnConfig.getProperty().replace('.', '-'));
			if (columnConfig.isSortable()) {
				css.append(" sortable");
			}
		}
		if (columnConfig.getCssClass() != null) {
			css.append(' ');
			css.append(columnConfig.getCssClass());
		}
		return css.toString();
	}
	
	/**
	 * Builds a collection of rows.
	 */
	protected final Collection buildRows() {
		Collection rows = new LinkedList();
		index = getParams().getOffset();
		Iterator it = items.iterator();
		while (it.hasNext()) {
			item = it.next();
			rows.add(buildRow());
			index++;
		}
		return rows;
	}

	/**
	 * Builds the current row.
	 */
	protected final ListRow buildRow() {
		BeanWrapper beanWrapper = new BeanWrapperImpl(item);
		objectId = beanWrapper.getPropertyValue(
				getListConfig().getIdProperty()).toString();
		
		rowStyle = StringUtils.unqualify(item.getClass().getName());
		
		if (getListConfig().getRowStyleProperty() != null) {
			try {
				rowStyle = FormatUtils.toCssClass(beanWrapper.getPropertyValue(
						getListConfig().getRowStyleProperty()).toString());
			}
			catch (NullValueInNestedPathException e) {
			}
		}
		
		Collection cells = buildCells(beanWrapper);
		ListRow row = new ListRow(rowStyle, objectId, cells);
		return row;
	}
	
	public void addRowStyle(String className) {
		rowStyle += " " + className;
	}
	
	/**
	 * Builds a collection of cells for the current row.
	 */
	protected Collection buildCells(BeanWrapper beanWrapper) {
		LinkedList cells = new LinkedList();
		Iterator it = getListConfig().getColumnConfigs().iterator();
		while (it.hasNext()) {
			cells.add(buildCell((ColumnConfig) it.next(), beanWrapper));
		}
		return cells;
	}

	/**
	 * Builds the current cell.
	 */
	protected final ListCell buildCell(ColumnConfig columnConfig, 
			BeanWrapper beanWrapper) {
		
		this.columnConfig = columnConfig;
		command = columnConfig.getCommand();
		String property = columnConfig.getProperty();
		if (property != null) {
			try {
				value = beanWrapper.getPropertyValue(property);
			}
			catch (NullValueInNestedPathException e) {
				value = null;
			}
		}
		else {
			value = null;
		}
		
		StringWriter writer = new StringWriter();
		columnConfig.getRenderer().render(this, new PrintWriter(writer));
		return new ListCell(getCssClass().toString(), writer.toString());
	}
	
	
	protected Collection buildCommands() {
		item = null;
		objectId = null;
		value = null;
		CellRenderer commandRenderer = getListConfig().getListCommandRenderer();
		LinkedList commands = new LinkedList();
		Iterator it = getListConfig().getCommands().iterator();
		while (it.hasNext()) {
			command = (Command) it.next();
			StringWriter writer = new StringWriter();
			commandRenderer.render(this, new PrintWriter(writer));
			commands.add(writer.toString());
		}
		return commands;
	}
	
	/**
	 * Builds a pager.
	 */
	protected Pager buildPager() {
		Pager pager = null;
		if (items.size() < itemsTotal) {
			int page = getParams().getOffset() / getParams().getPageSize() + 1;
			pager = new Pager(page, getParams().getPageSize(), itemsTotal);
			pager.setCopyParameters(false);
			pager.initialize(getListContext().getRequest(), 6, 
					Constants.PARAM_PAGE);
		}
		return pager;
	}
	
	/**
	 * Returns the absolute index of the current item in the model.
	 * @see org.riotfamily.riot.list.ui.render.RenderContext#getRowIndex()
	 */
	public int getRowIndex() {
		return index;
	}

	/**
	 * Returns the total number of list items. This value may be greater 
	 * than the number of items actually being displayed since typically a
	 * pageSize is passed to the {@link org.riotfamily.riot.dao.RiotDao 
	 * RiotDao} which limits the number of rows.
	 * 
	 * @see org.riotfamily.riot.list.ui.render.RenderContext#getItemsTotal()
	 */
	public int getItemsTotal() {
		return itemsTotal;
	}
	
	/**
	 */
	public String getProperty() {
		return columnConfig.getProperty();
	}

	/**
	 * Returns the value of the property to be displayed in the current cell. 
	 * @see org.riotfamily.riot.list.ui.render.RenderContext#getValue()
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * Returns the bean to be displayed in the current row.
	 * @see org.riotfamily.riot.list.command.CommandContext#getItem()
	 */
	public Object getItem() {
		return item;
	}
	
	public void setItem(Object item) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @see org.riotfamily.riot.list.ui.render.RenderContext#getCommand()
	 */
	public Command getCommand() {
		return command;
	}
	/**
	 * Returns the id of the current item.
	 * @see org.riotfamily.riot.list.command.CommandContext#getObjectId()
	 */
	public String getObjectId() {
		return objectId;
	}
	
	public boolean isConfirmed() {
		return false;
	}

	public ColumnConfig getColumnConfig() {
		return this.columnConfig;
	}

	public Locale getLocale() {
		return this.locale;
	}
	
	public String getContextPath() {
		return getListContext().getRequest().getContextPath();
	}
	
	public String encodeURL(String url) {
		return getListContext().getResponse().encodeURL(url);
	}
}
