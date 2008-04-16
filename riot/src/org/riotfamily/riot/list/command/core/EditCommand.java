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

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.editor.ObjectEditorDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.springframework.util.Assert;


/**
 * Command that displays the editor associated with the current list.
 */
public class EditCommand extends AbstractCommand {
	
	private static final String ACTION_EDIT = "edit";
	
	private static final String ACTION_ADD = "add";
	
	protected String getAction(CommandContext context) {
		if (context.getObjectId() == null) {
			return ACTION_ADD;
		}
		ObjectEditorDefinition def = context.getListDefinition().getDisplayDefinition();
		if (def instanceof FormDefinition) {
			return ACTION_EDIT;
		}
		return StepIntoCommand.ACTION_STEP_INTO;
	}
	
	public CommandResult execute(CommandContext context) {
		EditorDefinition def = context.getListDefinition().getDisplayDefinition();
		Assert.notNull(def, "A DisplayDefinition must be set in order to use the EditCommand.");
		return new GotoUrlResult(context, def.getEditorUrl(
				context.getObjectId(), context.getParentId(), 
				context.getParentEditorId()));
	}
}
