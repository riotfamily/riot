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
package org.riotfamily.core.screen.list.command.impl.clipboard;

import org.riotfamily.core.dao.CopyAndPasteEnabledDao;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.core.screen.list.command.result.UpdateCommandsResult;
import org.springframework.util.Assert;

public class CopyCommand extends AbstractCommand implements ClipboardCommand {

	@Override
	protected String getIcon(String action) {
		return "page_copy";
	}
	
	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		CopyAndPasteEnabledDao dao = getDao(context.getScreen());
		for (SelectionItem item : selection) {
			if (!dao.canCopy(item.getObject())) {
				return false;
			}
		}
		return true;
	}
	
	private CopyAndPasteEnabledDao getDao(ListScreen screen) {
		Assert.isInstanceOf(CopyAndPasteEnabledDao.class, screen.getDao());
		return (CopyAndPasteEnabledDao) screen.getDao();
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		Clipboard.get(context).set(context.getScreen(), selection, this);
		return new BatchResult(
				new UpdateCommandsResult(),
				new NotificationResult(context, this)
					.setArgs(selection.size())
					.setDefaultMessage("{0,choice,1#Item|1<{0} items} put into the clipboard"));
	}
	
	public boolean canPaste(ListScreen source, Selection selection, 
			CommandContext context, SelectionItem parentItem) {

		CopyAndPasteEnabledDao dao = getDao(context.getScreen());
		Object parent = getParent(parentItem, context);
		
		for (SelectionItem item : selection) {
			if (!dao.canPasteCopy(item.getObject(), parent)) {
				return false;
			}
		}
		return true;
	}

	public void paste(ListScreen source, Selection selection, 
			CommandContext context, SelectionItem parentItem, 
			NotificationResult notification) {
		
		CopyAndPasteEnabledDao dao = getDao(context.getScreen());
		Object parent = getParent(parentItem, context);

		for (SelectionItem item : selection) {
			dao.pasteCopy(item.getObject(), parent);
		}
		
		notification.setDefaultMessage("{0,choice,1#Item has|1<{0} items have} been copied.");
	}

	private Object getParent(SelectionItem parentItem, CommandContext context) {
		Object parent = parentItem.getObject();
		if (parent == null) {
			parent = context.getParent();
		}
		return parent;
	}
}
