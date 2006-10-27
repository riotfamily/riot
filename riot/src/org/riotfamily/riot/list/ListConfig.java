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
package org.riotfamily.riot.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.ui.render.CellRenderer;

/**
 *
 */
public class ListConfig {	
	
	private static final String DEFAULT_ID_PROPERTY = "id";

	private String id;

	private RiotDao dao;

	private List columnConfigs = new ArrayList();

	private List commands = new ArrayList();
	
	private ArrayList columnCommands = null;
	
	private String defaultCommandId;

	private CellRenderer listCommandRenderer;

	private String idProperty;
	
	private String rowStyleProperty;
	
	private int pageSize;
	
	private String filterFormId;
	
	private Order defaultOrder;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdProperty() {
		return idProperty != null ? idProperty : DEFAULT_ID_PROPERTY;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public String getRowStyleProperty() {
		return this.rowStyleProperty;
	}

	public void setRowStyleProperty(String rowStyleProperty) {
		this.rowStyleProperty = rowStyleProperty;
	}

	public void setDao(RiotDao dao) {
		this.dao = dao;
	}

	public RiotDao getDao() {
		return dao;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void addColumnConfig(ColumnConfig column) {
		columnConfigs.add(column);
	}

	public List getColumnConfigs() {
		return columnConfigs;
	}

	public ColumnConfig getColumnConfig(int index) {
		return (ColumnConfig) columnConfigs.get(index);
	}
	
	public ColumnConfig getColumnConfig(String property) {		
		property = property.trim();		
		Iterator it = columnConfigs.iterator();
		while (it.hasNext()) {
			ColumnConfig columnConfig = (ColumnConfig) it.next();			
			if (columnConfig.getProperty().equals(property)){
				return columnConfig;
			}
		}
		return null;
	}

	public void addCommand(Command command) {
		commands.add(command);
	}

	public List getCommands() {
		return commands;
	}

	public CellRenderer getListCommandRenderer() {
		return listCommandRenderer;
	}

	public void setListCommandRenderer(CellRenderer commandRenderer) {
		this.listCommandRenderer = commandRenderer;
	}
	
	public Class getItemClass() {
		return dao.getEntityClass();
	}

	public String getFirstProperty() {
		Iterator it = columnConfigs.iterator();
		while (it.hasNext()) {
			ColumnConfig columnConfig = (ColumnConfig) it.next();
			if (columnConfig.getProperty() != null) {
				return columnConfig.getProperty(); 
			}
		}
		return null;
	}
	
	public String getFirstSortableProperty() {
		Iterator it = columnConfigs.iterator();
		while (it.hasNext()) {
			ColumnConfig columnConfig = (ColumnConfig) it.next();
			if (columnConfig.getProperty() != null 
					&& columnConfig.isSortable()) {
				return columnConfig.getProperty(); 
			}
		}
		return null;
	}
	
	public List getColumnCommands() {
		if (columnCommands == null) {
			columnCommands = new ArrayList();
			Iterator it = columnConfigs.iterator();
			while (it.hasNext()) {
				ColumnConfig col = (ColumnConfig) it.next();
				if (col.getCommand() != null) {
					columnCommands.add(col.getCommand());
				}
			}
		}
		return columnCommands;
	}
	
	public Command getFirstColumnCommand() {
		List commands = getColumnCommands();
		if (commands != null && !commands.isEmpty()) {
			return (Command) commands.get(0);
		}
		return null;
	}

	public String getFilterFormId() {
		return filterFormId;
	}

	public void setFilterFormId(String filterForm) {
		this.filterFormId = filterForm;
	}

	public String getDefaultCommandId() {
		if (defaultCommandId == null) {
			Command command = getFirstColumnCommand();
			defaultCommandId = command != null ? command.getId() : ""; 
		}
		return defaultCommandId;
	}

	public void setDefaultCommandId(String defaultCommandId) {
		this.defaultCommandId = defaultCommandId;
	}

	public void setOrderBy(String orderBy) {		
		int i = orderBy.indexOf(' ');
		if (i != -1) {
			String property = orderBy.substring(0, i);
			boolean asc = !orderBy.endsWith(" desc");
			ColumnConfig col = getColumnConfig(property);
			boolean caseSensitive = (col == null) ? true : col.isCaseSensitive();			
			defaultOrder = new Order(property, asc, caseSensitive);
		}
		else {
			ColumnConfig col = getColumnConfig(orderBy);
			boolean caseSensitive = (col == null) ? true : col.isCaseSensitive();
			defaultOrder = new Order(orderBy, true, caseSensitive);
		}
	}
	
	public Order getDefaultOrder() {
		if (defaultOrder == null) {
			String property = getFirstSortableProperty();			
			if (property != null) {
				ColumnConfig col = getColumnConfig(property);	
				boolean caseSensitive = (col == null) ? true : col.isCaseSensitive();
				defaultOrder = new Order(property, true, caseSensitive);				
			}
		}		
		return defaultOrder;
	}	
	
}
