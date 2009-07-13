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
package org.riotfamily.core.screen.list.command.impl;

import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.dialog.YesNoCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public class DeleteCommand extends YesNoCommand {

	public boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() > 0) {
			RiotDao dao = context.getScreen().getDao();
			for (SelectionItem item : selection) {
				if (!dao.canDelete(item.getObject())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected String[] getCodes(CommandContext context, Selection selection) {
		return new String[] {
			"confirm.delete." + context.getScreen().getId(),
			"confirm.delete." + context.getScreen().getDao().getEntityClass(),
			"confirm.delete"
		};
	}
	
	protected Object[] getArgs(CommandContext context, Selection selection) {
		String label = null;
		if (selection.size() == 1) {
			label = context.getScreen().getItemLabel(selection.getSingleItem().getObject());
		}
		return new Object[] { selection.size(),	label };
	}
	
	protected String getDefaultMessage(CommandContext context, Selection selection) {
		return "Do you really want to delete {0,choice,1#\"{1}\"|1<the{0} selected items}?";
	}
	
	@Override
	protected CommandResult handleYes(CommandContext context,
			Selection selection, Object input) {
	
		Object[] args = getArgs(context, selection);
		for (SelectionItem item : selection) {
			context.getScreen().getDao().delete(item.getObject(), context.getParent());
		}
		return new BatchResult(
			new RefreshListResult(),
			new NotificationResult(context, this)
				.setDefaultMessage("{0,choice,1#Item \"{1}\"|1<{0} items} successfully deleted.")
				.setArgs(args));
	}

}
