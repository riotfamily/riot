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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.command;

import java.util.Collections;
import java.util.List;

import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.list.command.BatchCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.CommandState;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class PublishPageCommand extends AbstractCommand implements BatchCommand {

	public static final String ACTION_PUBLISH = "publish";
	
	private PageDao pageDao;
	
	public PublishPageCommand(PageDao pageDao) {
		this.pageDao = pageDao;
	}
	
	protected String getAction(CommandContext context) {
		return ACTION_PUBLISH;
	}
	
	public boolean isBatchCommand() {
		return true;
	}
	
	protected boolean isEnabled(CommandContext context, String action) {
		Page page = (Page) context.getBean();
		return (!page.isPublished() || page.isDirty()) 
				&& PageCommandUtils.isTranslated(context);
	}

	public CommandResult execute(CommandContext context) {
		Page page = (Page) context.getBean();
		pageDao.publishPage(page);
		return new RefreshSiblingsResult(context);
	}

	public String getConfirmationMessage(CommandContext context) {
		Page page = (Page) context.getBean();
		return context.getMessageResolver().getMessage("confirm.publishPage",
				new Object[] {page.getTitle(true)},
				"Do you really want to publish this page?");
	}

	public String getBatchConfirmationMessage(CommandContext context, String action) {
		return context.getMessageResolver().getMessage("confirm.publishPage.selected",
				"Do you really want to publish the selected pages?");
	}
	
	public List<CommandState> getBatchStates(CommandContext context) {
		return Collections.singletonList(getState(context, ACTION_PUBLISH));
	}

}
