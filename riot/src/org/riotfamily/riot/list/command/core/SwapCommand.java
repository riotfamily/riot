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

import org.riotfamily.riot.dao.SwappableItemDao;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ReloadResult;

/**
 * Command that swaps two items in a list.
 */
public class SwapCommand extends AbstractCommand {
	    
    private int swapWith;
    
	public void setSwapWith(int swapWith) {
		this.swapWith = swapWith;
	}
		
	protected boolean isEnabled(CommandContext context, String action) {
		if (context.getDao() instanceof SwappableItemDao) {
			int index = context.getParams().getOffset() + context.getRowIndex(); 
			return index + swapWith >= 0 && 
					index + swapWith < context.getItemsTotal();
		}
		return false;
	}

	public CommandResult execute(CommandContext context) {
		SwappableItemDao dao = (SwappableItemDao) context.getDao();
		ListDefinition listDef = context.getListDefinition();
		String parentId = context.getParentId();
		Object parent = EditorDefinitionUtils.loadParent(listDef, parentId);
		
		dao.swapEntity(context.getBean(), parent, context.getParams(),
				context.getRowIndex() + swapWith);
		
		return new ReloadResult();
	}
    
}
