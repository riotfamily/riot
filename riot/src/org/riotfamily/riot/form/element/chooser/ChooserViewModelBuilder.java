package org.riotfamily.riot.form.element.chooser;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ui.ListContext;
import org.riotfamily.riot.list.ui.ViewModelBuilder;
import org.springframework.beans.BeanWrapper;

public class ChooserViewModelBuilder extends ViewModelBuilder {

	private ColumnConfig[] extraColumns;
	
	public ChooserViewModelBuilder(ListContext context, 
			ColumnConfig[] extraColumns) {
		
		super(context);
		this.extraColumns = extraColumns;
	}

	protected Collection buildHeadings() {
		LinkedList headings = new LinkedList();
		Iterator it = getListConfig().getColumnConfigs().iterator();
		while (it.hasNext()) {
			ColumnConfig columnConfig = (ColumnConfig) it.next();
			if (columnConfig.getCommand() == null) {
				headings.add(buildHeading(columnConfig));
			}
		}
		for (int i = 0; i < extraColumns.length; i++) {
			headings.add(buildHeading(extraColumns[i]));
		}
		return headings;
	}
	
	protected Collection buildCells(BeanWrapper beanWrapper) {
		LinkedList cells = new LinkedList();
		Iterator it = getListConfig().getColumnConfigs().iterator();
		while (it.hasNext()) {
			ColumnConfig columnConfig = (ColumnConfig) it.next();
			if (columnConfig.getCommand() == null) {
				cells.add(buildCell(columnConfig, beanWrapper));
			}
		}
		for (int i = 0; i < extraColumns.length; i++) {
			cells.add(buildCell(extraColumns[i], beanWrapper));
		}
		return cells;
	}
	
	public String getDefaultCommandId() {
		return extraColumns[0].getCommand().getId();
	}
	
	protected Collection buildCommands() {
		return null;
	}
	
}
