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
