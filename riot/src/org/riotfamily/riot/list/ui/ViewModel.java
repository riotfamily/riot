package org.riotfamily.riot.list.ui;

import java.util.Collection;

import org.riotfamily.common.web.view.Pager;

/**
 * View model that is exposed to the actual list view in order to render a list.
 * 
 * @see org.riotfamily.riot.list.ui.ListController
 * @see org.riotfamily.riot.list.ui.ViewModelBuilder
 */
public class ViewModel {

	private String editorId;

	private String cssClass;
	
	private String parentId;

	private Collection headings;

	private Collection rows;

	private Collection commands;
	
	private String defaultCommandId;

	private Pager pager;

	public ViewModel(String editorId, String parentId, 
			Collection headings, Collection rows, Collection commands, 
			String defaultCommandId, Pager pager, String cssClass) {

		this.editorId = editorId;
		this.parentId = parentId;
		this.headings = headings;
		this.rows = rows;
		this.commands = commands;
		this.defaultCommandId = defaultCommandId;
		this.pager = pager;
		this.cssClass = cssClass;
	}

	public String getEditorId() {
		return editorId;
	}
	
	public String getCssClass() {
		return this.cssClass;
	}

	public String getParentId() {
		return parentId;
	}

	public Collection getHeadings() {
		return headings;
	}

	public int getColCount() {
		return headings.size();
	}

	public Collection getRows() {
		return rows;
	}

	public Collection getCommands() {
		return commands;
	}

	public String getDefaultCommandId() {
		return defaultCommandId;
	}

	public Pager getPager() {
		return pager;
	}

}
