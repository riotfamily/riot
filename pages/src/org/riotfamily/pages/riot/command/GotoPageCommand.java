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

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SimpleBatchCommand;
import org.riotfamily.core.screen.list.command.result.CommandResult;
import org.riotfamily.core.screen.list.command.result.PopupResult;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.view.PageFacade;

public class GotoPageCommand extends SimpleBatchCommand<Page> {

	@Override
	protected boolean isEnabledForAll(CommandContext context, Selection selection) {
		return true;
	}
	
	@Override
	protected boolean isShowOnForm(CommandContext context) {
		return true;
	}
	
	@Override
	protected String getStyleClass(CommandContext context) {
		return "browse";
	}

	@Override
	protected CommandResult execute(CommandContext context, Page page, 
			int index, int selectionSize) {
		
		String url = new PageFacade(page, context.getRequest()).getUrl();
		return new PopupResult(context.getRequest().getContextPath() + url);
	}

}
