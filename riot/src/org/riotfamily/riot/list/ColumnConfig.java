package org.riotfamily.riot.list;

import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.ui.render.CellRenderer;

/**
 *
 */
public class ColumnConfig {
	
	private String property;

	private int lookupLevel;

	private boolean sortable;

	private boolean ascending = true;
	
	private boolean caseSensitive = true;

	private Command command;

	private CellRenderer renderer;

	private CellRenderer headingRenderer;

	private String cssClass;

	
	public ColumnConfig() {
	}
	
	public ColumnConfig(Command command, CellRenderer renderer, 
			CellRenderer headingRenderer) {
		
		this.command = command;
		this.renderer = renderer;
		this.headingRenderer = headingRenderer;
	}

	public int getLookupLevel() {
		return lookupLevel;
	}

	public void setLookupLevel(int lookupLevel) {
		this.lookupLevel = lookupLevel;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isSortable() {
		return sortable && property != null;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}	

	public boolean isCaseSensitive() {		
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {		
		this.caseSensitive = caseSensitive;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public CellRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(CellRenderer renderer) {
		this.renderer = renderer;
	}

	public CellRenderer getHeadingRenderer() {
		return headingRenderer;
	}

	public void setHeadingRenderer(CellRenderer headingRenderer) {
		this.headingRenderer = headingRenderer;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
}
