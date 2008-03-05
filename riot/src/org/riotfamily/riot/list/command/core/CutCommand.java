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

import java.util.Collections;
import java.util.List;

import org.riotfamily.riot.list.command.BatchCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public class CutCommand extends AbstractCommand implements BatchCommand {

	public static final String ACTION_CUT = "cut";

	protected String getAction(CommandContext context) {
		return ACTION_CUT;
	}

	protected String getItemStyleClass(CommandContext context, String action) {
		return Clipboard.get(context).isCut(context) ? "cut" : null;
	}

	protected boolean isEnabled(CommandContext context, String action) {
		return Clipboard.get(context).canCut(context);
	}

	public CommandResult execute(CommandContext context) {
		Clipboard.get(context).cut(context);
		return new RefreshSiblingsResult(context);
	}

	public String getBatchConfirmationMessage(CommandContext context, String action) {
		return null;
	}

	public List getBatchStates(CommandContext context) {
		return Collections.singletonList(getState(context, ACTION_CUT));
	}
}
