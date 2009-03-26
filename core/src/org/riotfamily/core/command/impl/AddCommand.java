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
package org.riotfamily.core.command.impl;

import org.riotfamily.core.command.Command;
import org.riotfamily.core.command.CommandContext;
import org.riotfamily.core.command.CommandInfo;
import org.riotfamily.core.command.CommandResult;
import org.riotfamily.core.command.Selection;
import org.riotfamily.core.command.result.GotoUrlResult;
import org.riotfamily.core.screen.ScreenContext;

public class AddCommand implements Command {

	public CommandInfo getInfo(CommandContext context) {
		CommandInfo info = new CommandInfo();
		info.setLabel("Add");
		info.setStyleClass("add");
		return info;
	}
	
	public boolean isEnabled(CommandContext context, Selection selection) {
		return selection.size() <= 1;
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		ScreenContext childContext = context.createNewItemContext(selection.getSingleObject());
		return new GotoUrlResult(context.getRequest(), childContext.getUrl());
	}

}
