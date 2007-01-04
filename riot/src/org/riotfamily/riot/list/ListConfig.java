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

import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.form.command.FormCommand;
import org.riotfamily.riot.list.command.Command;

/**
 *
 */
public class ListConfig {	
	
	private static final String DEFAULT_ID_PROPERTY = "id";

	private String id;

	private RiotDao dao;

	private List columnConfigs = new ArrayList();

	private List commands = new ArrayList();
	
	private ArrayList columnCommands = new ArrayList();
	
	private List formCommands;
	
	private String[] defaultCommandIds;

	private String idProperty;
	
	private String rowStyleProperty;
	
	private int pageSize = 15;
	
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
	
	public void addColumnCommand(Command command) {
		columnCommands.add(command);
	}
	
	public List getColumnCommands() {
		return columnCommands;
	}
	
	public List getFormCommands() {
		if (formCommands == null) {
			formCommands = new ArrayList();
			Iterator it = columnCommands.iterator();
			while (it.hasNext()) {
				Object command = it.next();
				if (command instanceof FormCommand) {
					formCommands.add(command);
				}
			}
		}
		return formCommands;
	}
	
	public Command getFirstColumnCommand() {
		if (!columnCommands.isEmpty()) {
			return (Command) columnCommands.get(0);
		}
		return null;
	}

	public String getFilterFormId() {
		return filterFormId;
	}

	public void setFilterFormId(String filterForm) {
		this.filterFormId = filterForm;
	}

	public void setDefaultCommandId(String defaultCommandId) {
		setDefaultCommandIds(new String[] {defaultCommandId});
	}
	
	public void setDefaultCommandIds(String[] defaultCommandIds) {
		this.defaultCommandIds = defaultCommandIds;
	}

	public String[] getDefaultCommandIds() {
		if (defaultCommandIds == null) {
			Command command = getFirstColumnCommand();
			if (command != null) {
				defaultCommandIds = new String[] {command.getId()};	
			}
		}
		return defaultCommandIds;
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
