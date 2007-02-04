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
package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.support.ListParamsImpl;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DescendCommand extends AbstractCommand {

	public static final String ID = "descend";
	
	private ListDefinition nextListDefinition;
	
	private EditorDefinition targetDefinition;
	
	private static ListParams params = new ListParamsImpl();
	
	public DescendCommand(ListDefinition listDefinition, 
			EditorDefinition targetDefinition) {
		
		this.nextListDefinition = listDefinition;
		this.targetDefinition = targetDefinition;
	}

	public String getId() {
		return ID;
	}
	
	public boolean isEnabled(CommandContext context) {
		RiotDao dao = nextListDefinition.getListConfig().getDao();
		int size = dao.getListSize(context.getBean(), params);
		if (size == -1) {
			size = dao.list(context.getBean(), params).size();
		}
		return size > 0;
	}
	
	public CommandResult execute(CommandContext context) {
		return new GotoUrlResult(context, nextListDefinition.getEditorUrl(
				null, context.getObjectId()) + "?choose=" 
				+ targetDefinition.getId());
	}
}
