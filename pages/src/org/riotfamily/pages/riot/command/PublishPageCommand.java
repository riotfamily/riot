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

import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.ReloadResult;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class PublishPageCommand extends AbstractCommand {

	public static final String ACTION_PUBLISH = "publishPage";

	public static final String ACTION_UNPUBLISH = "unpublishPage";

	private PageDao pageDao;
	
	
	public PublishPageCommand(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	protected String getAction(CommandContext context) {
		Page page = (Page) context.getBean();
		return page.isPublished() ? ACTION_UNPUBLISH : ACTION_PUBLISH;
	}

	protected boolean isEnabled(CommandContext context, String action) {
		return !PageCommandUtils.isSystemPage(context)
				&& PageCommandUtils.isTranslated(context);
	}

	public CommandResult execute(CommandContext context) {
		Page page = (Page) context.getBean();
		pageDao.publishPage(page);
		return new ReloadResult();
	}

	public String getConfirmationMessage(CommandContext context) {
		Page page = (Page) context.getBean();
		if (page.isPublished()) {
			return context.getMessageResolver().getMessage("confirm.unpublish",
					new Object[] {page.getPathComponent()},
					"Do you really want to unpublish this page?");
		}
		else {
			return context.getMessageResolver().getMessage("confirm.publish",
					new Object[] {page.getPathComponent()},
					"Do you really want to publish this page?");
		}
	}

}
