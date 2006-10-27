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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.ui.render.CellRenderer;
import org.riotfamily.riot.list.ui.render.CommandRenderer;
import org.riotfamily.riot.list.ui.render.HeadingRenderer;
import org.riotfamily.riot.list.ui.render.ObjectRenderer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 */
public class ListRepository implements ApplicationContextAware {

	private Log log = LogFactory.getLog(ListRepository.class);
	
	private HashMap listConfigs = new HashMap();
	
	private HashMap commands = new HashMap();
	
	private ApplicationContext applicationContext;
	
	private CellRenderer defaultHeadingRenderer;
	
	private CellRenderer itemCommandRenderer;
	
	private CellRenderer listCommandRenderer;
	
	private CellRenderer defaultCellRenderer;
	
	private int defaultPageSize = 50;

	public ListRepository() {
		setDefaultHeadingRenderer(new HeadingRenderer());
		setDefaultCellRenderer(new ObjectRenderer());
		setItemCommandRenderer(new CommandRenderer());
		
		CommandRenderer renderer = new CommandRenderer();
		renderer.setRenderDisabled(false);
		renderer.setRenderText(false);
		setListCommandRenderer(renderer);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		log.debug("Looking up command implementations ...");
		Map commands = applicationContext.getBeansOfType(Command.class);
		Iterator it = commands.values().iterator();
		while (it.hasNext()) {
			Command command = (Command) it.next();
			this.commands.put(command.getId(), command);
		}
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
		Command command = (Command) commands.get(commandId);
		if (command == null) {
			log.error("No such command: " + commandId);
		}
		return command;
	}

	public ListConfig getListConfig(String listId) {
		return (ListConfig) listConfigs.get(listId);
	}
	
	public void addListConfig(ListConfig listConfig) {
		listConfigs.put(listConfig.getId(), listConfig);
		if (listConfig.getPageSize() == 0) {
			listConfig.setPageSize(defaultPageSize);
		}
	}
	
	public HashMap getListConfigs() {
		return listConfigs;
	}

	public CellRenderer getItemCommandRenderer() {
		return itemCommandRenderer;
	}

	public void setItemCommandRenderer(CellRenderer commandRenderer) {
		this.itemCommandRenderer = commandRenderer;
	}
	
	public CellRenderer getListCommandRenderer() {
		return listCommandRenderer;
	}
	
	public void setListCommandRenderer(CellRenderer listCommandRenderer) {
		this.listCommandRenderer = listCommandRenderer;
	}

	public CellRenderer getDefaultCellRenderer() {
		return defaultCellRenderer;
	}

	public void setDefaultCellRenderer(CellRenderer defaultCellRenderer) {
		this.defaultCellRenderer = defaultCellRenderer;
	}
	
	public CellRenderer getDefaultHeadingRenderer() {
		return defaultHeadingRenderer;
	}

	public void setDefaultHeadingRenderer(CellRenderer defaultHeadingRenderer) {
		this.defaultHeadingRenderer = defaultHeadingRenderer;
	}

	public int getDefaultPageSize() {
		return defaultPageSize;
	}

	public void setDefaultPageSize(int defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}
	
}
