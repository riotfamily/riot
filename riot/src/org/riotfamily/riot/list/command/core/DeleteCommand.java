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

import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.command.BatchCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ShowListResult;

/**
 * Command that deletes an item. To prevent accidental deletion a confirmation
 * message is displayed.
 */
public class DeleteCommand extends AbstractCommand implements BatchCommand {

	public static final String ACTION_DELETE = "delete";
	
	public DeleteCommand() {
		setShowOnForm(true);
	}
		
	protected String getAction(CommandContext context) {
		return ACTION_DELETE;
	}
	
	protected boolean isEnabled(CommandContext context, String action) {
		return context.getObjectId() != null;
	}
		
	public String getConfirmationMessage(CommandContext context) {
		Object[] args = getDefaultMessageArgs(context);
		return context.getMessageResolver().getMessage("confirm.delete", args, 
				"Do you really want to delete this element?");
	}

	public String getBatchConfirmationMessage(CommandContext context, String action) {
		return context.getMessageResolver().getMessage("confirm.delete.selected", 
				"Do you really want to delete all selected elements?");
	}
	
	public List getBatchStates(CommandContext context) {
		return Collections.singletonList(getState(context, ACTION_DELETE));
	}
	
	public CommandResult execute(CommandContext context) {
		ListDefinition listDef = context.getListDefinition();
		String parentId = context.getParentId();
		Object parent = EditorDefinitionUtils.loadParent(listDef, parentId);
		
		Object item = context.getBean();
		context.getDao().delete(item, parent);
		return new ShowListResult(context);
	}
	
}
