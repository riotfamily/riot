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

import org.riotfamily.core.dao.SwappableItemDao;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public abstract class SwapCommand extends AbstractCommand {

	protected abstract int getSwapWith();

	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() == 1 && context.getScreen().getDao() instanceof SwappableItemDao) {
			SwappableItemDao dao = (SwappableItemDao) context.getScreen().getDao();
			return dao.canSwap(selection.getSingleItem().getObject(), 
					context.getParent(), context.getParams(), getSwapWith());
		}
		return false;
	}

	public CommandResult execute(CommandContext context, Selection selection) {
		SwappableItemDao dao = (SwappableItemDao) context.getScreen().getDao();
		dao.swapEntity(selection.getSingleItem().getObject(), context.getParent(), 
				context.getParams(), getSwapWith());
		
		return new RefreshListResult(selection.getSingleItem().getParentNodeId());
	}

}
