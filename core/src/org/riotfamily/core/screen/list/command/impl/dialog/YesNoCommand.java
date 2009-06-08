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
import org.riotfamily.forms.element.StaticText;

public class YesNoCommand extends DialogCommand {

	@Override
	public Form createForm(CommandContext context, Selection selection) {
		Form form = new Form(HashMap.class);
		addButton(form, "yes");
		addButton(form, "no");
		addQuestion(form, context, selection);
		addExtraElements(form, context, selection);
		return form;
	}
	
	private void addQuestion(Form form, CommandContext context, Selection selection) {
		String question = getQuestion(context, selection);
		if (question != null) {
			form.addElement(new StaticText(question));
		}
	}
	
	protected String getQuestion(CommandContext context, Selection selection) {
		String[] codes = getCodes(context, selection);
		if (codes == null) {
			return null;
		}
		return context.getMessageResolver().getMessage(
				codes, 
				getArgs(context, selection), 
				getDefaultMessage(context, selection));
	}
	
	protected String[] getCodes(CommandContext context, Selection selection) {
		return null;
	}

	protected Object[] getArgs(CommandContext context, Selection selection) {
		return null;
	}

	protected String getDefaultMessage(CommandContext context, Selection selection) {
		return null;
	}
	
	protected void addExtraElements(Form form, CommandContext context, Selection selection) {
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
