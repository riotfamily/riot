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
