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
import org.riotfamily.pages.dao.PageValidationUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.EditCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public class TranslatePageCommand extends EditCommand {

	public static final String ACTION_TRANSLATE = "translate";
	
	private PageDao pageDao;

	public TranslatePageCommand(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	@Override
	public boolean isEnabled(CommandContext context) {
		Page page = PageCommandUtils.getPage(context);
		Site site = PageCommandUtils.getParentSite(context);
		return PageValidationUtils.isTranslatable(page, site);
	}
	
	@Override
	public String getAction() {
		return ACTION_TRANSLATE;
	}
	
	public String getItemStyleClass(CommandContext context) {
		if (!PageCommandUtils.isLocalPage(context)) {
			return "master";
		}
		return null;
	}

	public CommandResult execute(CommandContext context) {
		Page page = PageCommandUtils.getPage(context);
		Site site = PageCommandUtils.getParentSite(context);
		pageDao.addTranslation(page, site);
		return new RefreshSiblingsResult(context);
	}

}
