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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.list.command.BatchCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.BatchResult;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public class CutCommand extends AbstractCommand implements BatchCommand {

	public static final String ACTION_CUT = "cut";

	@Override
	public String getAction() {
		return ACTION_CUT;
	}

	@Override
	public String getItemStyleClass(CommandContext context) {
		return Clipboard.get(context).isCut(context) ? "cut" : null;
	}

	@Override
	public boolean isEnabled(CommandContext context) {
		return Clipboard.get(context).canCut(context);
	}

	public CommandResult execute(CommandContext context) {
		BatchResult result = new BatchResult();
		Clipboard clipboard = Clipboard.get(context);
		for (String objectId : clipboard.getObjectIds()) {
			result.add(new RefreshSiblingsResult(objectId));
		}
		clipboard.cut(context);
		result.add(new RefreshSiblingsResult(context));
		return result;
	}

	public String getBatchConfirmationMessage(CommandContext context) {
		return null;
	}

}
