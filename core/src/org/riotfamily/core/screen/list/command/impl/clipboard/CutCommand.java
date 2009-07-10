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

import org.riotfamily.core.dao.CutAndPasteEnabledDao;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.core.screen.list.command.result.UpdateCommandsResult;

public class CutCommand extends AbstractCommand implements ClipboardCommand {

	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		return !Clipboard.get(context).isAlreadySet(this, selection)
				&& canCut(context.getScreen(), selection);
	}
	
	protected boolean canCut(ListScreen source, Selection selection) {
		if (source.getDao() instanceof CutAndPasteEnabledDao) {
			CutAndPasteEnabledDao dao = (CutAndPasteEnabledDao) source.getDao();
			for (SelectionItem item : selection) {
				if (!dao.canCut(item.getObject())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean canPaste(ListScreen source, Selection selection, 
			ListScreen target, SelectionItem newParent) {
		
		if (target.getDao() instanceof CutAndPasteEnabledDao) {
			Object dest = newParent.getObject();
			CutAndPasteEnabledDao dao = (CutAndPasteEnabledDao) target.getDao();
			for (SelectionItem item : selection) {
				if (!dao.canPasteCut(item.getObject(), dest)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		Clipboard.get(context).set(context.getScreen(), selection, this);
		return new BatchResult(
				new UpdateCommandsResult(),
				new NotificationResult(context, this)
					.setArgs(selection.size())
					.setDefaultMessage("{0,choice,1#Item|1<{0} items} put into the clipboard"));
	}
	
	public void paste(ListScreen source, Selection selection, 
			ListScreen target, SelectionItem newParent, 
			NotificationResult notification) {
		
		CutAndPasteEnabledDao sourceDao = (CutAndPasteEnabledDao) source.getDao();
		CutAndPasteEnabledDao targetDao = (CutAndPasteEnabledDao) target.getDao();
		
		for (SelectionItem item : selection) {
			Object obj = item.getObject();
			if (sourceDao != null) {
				Object oldParent = sourceDao.getParent(obj);
				targetDao.pasteCut(obj, newParent.getObject());
				sourceDao.cut(obj, oldParent);
			}
			else {
				targetDao.pasteCut(obj, newParent.getObject());
			}
		}
		notification.setDefaultMessage("{0,choice,1#Item has|1<{0} items have} been moved.");
	}
}
