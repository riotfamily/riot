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

import org.riotfamily.core.dao.Constraints;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractChildCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;

public class AddCommand extends AbstractChildCommand {
	
	@Override
	protected boolean isEnabled(CommandContext context, SelectionItem parent) {
		RiotDao dao = context.getScreen().getDao();
		if (dao instanceof Constraints) {
			Constraints cd = (Constraints) dao;
			return cd.canAdd(parent.getObject());
		}
		return true;
	}
	
	@Override
	protected CommandResult execute(CommandContext context, SelectionItem parent) {
		ScreenContext childContext = context.createNewItemContext(parent.getObject());
		return new GotoUrlResult(context.getRequest(), childContext.getUrl());
	}

}
