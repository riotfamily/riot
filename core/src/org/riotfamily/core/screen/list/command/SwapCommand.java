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
package org.riotfamily.core.screen.list.command;

import org.riotfamily.core.dao.SwappableItemDao;
import org.riotfamily.core.screen.list.command.result.CommandResult;
import org.riotfamily.core.screen.list.command.result.RefreshSiblingsResult;

public class SwapCommand extends AbstractCommand {

	private int swapWith;

	public void setSwapWith(int swapWith) {
		this.swapWith = swapWith;
	}

	@Override
	protected String getAction(CommandContext context) {
		return swapWith > 0 ? "moveDown" : "moveUp";
	}

	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() == 1 && context.getDao() instanceof SwappableItemDao) {
			int index = selection.getLastRowIndex();
			return index + swapWith >= 0 &&
					index + swapWith < context.getItemsTotal();
		}
		return false;
	}

	public CommandResult execute(CommandContext context, Selection selection) {
		SwappableItemDao dao = (SwappableItemDao) context.getDao();
		dao.swapEntity(selection.getSingleObject(), context.getParent(), 
				context.getParams(), swapWith);
		
		return new RefreshSiblingsResult(selection.getSingleObjectId());
	}

}
