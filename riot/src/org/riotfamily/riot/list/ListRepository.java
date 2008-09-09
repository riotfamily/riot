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
package org.riotfamily.riot.list;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.ui.ObjectRenderer;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.ui.DefaultObjectRenderer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 */
public class ListRepository implements ApplicationContextAware {

	private RiotLog log = RiotLog.get(ListRepository.class);
	
	private HashMap<String, ListConfig> listConfigs = Generics.newHashMap();
	
	private HashMap<Class<?>, ListConfig> listConfigsByClass = Generics.newHashMap();
	
	private HashMap<String, Command> commands = Generics.newHashMap();
	
	private ApplicationContext applicationContext;
	
	private ObjectRenderer defaultCellRenderer = new DefaultObjectRenderer();
	
	private int defaultPageSize = 50;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		log.debug("Looking up command implementations ...");
		Map<String, Command> commands = SpringUtils.beansOfType(applicationContext, Command.class);
		Iterator<Command> it = commands.values().iterator();
		while (it.hasNext()) {
			Command command = it.next();
			this.commands.put(command.getId(), command);
		}
	}
	
	public void setRiotDaoService(RiotDaoService riotDaoService) {
		riotDaoService.setListRepository(this);
	}
	
	/**
	 * @return Returns the applicationContext.
	 */
	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public void addListener(ConfigurationEventListener listener) {
	}
	
	public Command getCommand(String commandId) {
		Command command = commands.get(commandId);
		if (command == null) {
			log.error("No such command: " + commandId);
		}
		return command;
	}

	public void addCommand(Command command) {
		String id = command.getId();
		Command existingCommand = commands.get(id);
		for (int i = 2; existingCommand != null; i++) {
			id = command.getId() + i;
			existingCommand = getCommand(id);
		}
		command.setId(id);
		commands.put(id, command);
	}
	
	public ListConfig getListConfig(String listId) {
		return (ListConfig) listConfigs.get(listId);
	}
	
	public ListConfig getListConfig(Class<?> entityClass) {
		return (ListConfig) listConfigsByClass.get(entityClass);
	}
	
	public void addListConfig(ListConfig listConfig) {
		listConfigs.put(listConfig.getId(), listConfig);
		listConfigsByClass.put(listConfig.getDao().getEntityClass(), listConfig);
		if (listConfig.getPageSize() == 0) {
			listConfig.setPageSize(defaultPageSize);
		}
	}
	
	protected Map<Class<?>, ListConfig> getListConfigsByClass() {
		return this.listConfigsByClass;
	}
	
	protected Map<String, ListConfig> getListConfigs() {
		return listConfigs;
	}
	
	public ObjectRenderer getDefaultCellRenderer() {
		return defaultCellRenderer;
	}

	public void setDefaultCellRenderer(ObjectRenderer defaultCellRenderer) {
		this.defaultCellRenderer = defaultCellRenderer;
	}
	
	public int getDefaultPageSize() {
		return defaultPageSize;
	}

	public void setDefaultPageSize(int defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}
	
}
