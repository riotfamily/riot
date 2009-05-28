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
package org.riotfamily.core.screen.list.command.impl.dialog;

import java.util.HashMap;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.forms.Form;

public class YesNoCommand extends DialogCommand {

	@Override
	public Form createForm(CommandContext context, Selection selection) {
		Form form = new Form(HashMap.class);
		addButton(form, "yes");
		addButton(form, "no");
		initForm(form, context, selection);
		return form;
	}
	
	protected void initForm(Form form, CommandContext context, Selection selection) {
	}
	
	@Override
	public CommandResult handleInput(CommandContext context,
			Selection selection, Object input, String button) {
	
		if ("yes".equals(button)) {
			return handleYes(context, selection, input);
		}
		return handleNo(context, selection, input);
	}
	
	protected CommandResult handleYes(CommandContext context,
			Selection selection, Object input) {
	
		return new NotificationResult(context).setMessage("Yes!");
	}
	
	protected CommandResult handleNo(CommandContext context,
			Selection selection, Object input) {
	
		return null;
	}
	
	
}
