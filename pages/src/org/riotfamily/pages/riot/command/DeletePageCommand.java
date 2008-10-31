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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.command;

import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.list.command.BatchCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class DeletePageCommand extends AbstractCommand implements BatchCommand {

	public static final String ACTION_DELETE = "delete";
	
	private PageDao pageDao;
	
	public DeletePageCommand(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public String getAction() {
		return ACTION_DELETE;
	}
	
	public boolean isEnabled(CommandContext context) {
		Page page = (Page) context.getBean();
		return PageCommandUtils.isTranslated(context) && !page.isPublished();
	}
	
	public String getConfirmationMessage(CommandContext context) {
		Page page = (Page) context.getBean();
		return context.getMessageResolver().getMessage("confirm.delete",
				new Object[] {page.getTitle()},
				"Do you really want to delete '"
				 + page.getTitle(true) + "'?");
	}
	
	public String getBatchConfirmationMessage(CommandContext context) {
		return context.getMessageResolver().getMessage("confirm.delete.selected", 
				"Do you really want to delete all selected pages?'");
	}
	
	public CommandResult execute(CommandContext context) {
		Page page = (Page) context.getBean();
		pageDao.deletePage(page);
		return new RefreshSiblingsResult(context);
	}
	
}
