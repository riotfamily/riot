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
package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.dao.CopyAndPasteEnabledDao;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.SetRowStyleResult;
import org.riotfamily.riot.list.command.result.ShowListResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.command.support.Clipboard;
import org.riotfamily.riot.list.ui.render.RenderContext;

public class CopyCommand extends AbstractCommand {

	private static final String COPY_ROW_STYLE = "copied";
	
	public boolean isEnabled(RenderContext context) {
		if (context.getDao() instanceof CopyAndPasteEnabledDao) {
			Clipboard cb = Clipboard.get(context);
			if (cb.isCopied(context)) {
				context.addRowStyle(COPY_ROW_STYLE);
			}
			return true;	
		}
		return false;
	}
	
	public CommandResult execute(CommandContext context) {
		Clipboard cb = Clipboard.get(context);
		boolean empty = cb.isEmpty();
		cb.copy(context);
		if (empty) {
			return new SetRowStyleResult(context.getObjectId(), COPY_ROW_STYLE);
		}
		else {
			return new ShowListResult(context);
		}
	}
	
}
