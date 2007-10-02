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

import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.core.DeleteCommand;
import org.springframework.web.util.HtmlUtils;

public class DeletePageCommand extends DeleteCommand {

	protected boolean isEnabled(CommandContext context, String action) {
		return super.isEnabled(context, action)
				&& !PageCommandUtils.isSystemPage(context)
				&& PageCommandUtils.isTranslated(context);
	}

	public String getConfirmationMessage(CommandContext context) {
		Page page = PageCommandUtils.getPage(context);
		String label = HtmlUtils.htmlEscape(page.getPathComponent());
		
		int numChilds = page.getChildPages().size();
		if (numChilds > 0) {
			return context.getMessageResolver().getMessage(
					"org.riotfamily.pages.confirm.delete.withChildren",
					new Object[] {label, new Integer(numChilds)}, 
					"Do you really want to delete this page and all of its child pages?");
		}
		return context.getMessageResolver().getMessage(
				"org.riotfamily.pages.confirm.delete",
				new Object[] {label}, 
				"Do you really want to delete this page?");
	}

}
