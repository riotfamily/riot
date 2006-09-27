package org.riotfamily.riot.list.ui.render;

import java.util.Locale;

import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandContext;

/**
 * Context passed to a {@link org.riotfamily.riot.list.ui.render.CellRenderer
 * CellRenderer} when rendering a list cell.
 */
public interface RenderContext extends CommandContext {

	public int getItemsTotal();
	
	public Object getValue();
	
	public Command getCommand();
	
	public String getProperty();
	
	public ColumnConfig getColumnConfig();
	
	public Locale getLocale();

	public String getContextPath();
	
	public String encodeURL(String url);

	public void addRowStyle(String className);

}
