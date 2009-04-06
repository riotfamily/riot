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
package org.riotfamily.core.screen.list.command;

import org.riotfamily.core.dao.TreeDao;
import org.riotfamily.core.screen.list.command.result.CommandResult;

public abstract class AbstractChildCommand extends AbstractCommand {

	@Override
	public final boolean isEnabled(CommandContext context, Selection selection) {
		return isEnabled(context, getParent(context, selection));
	}
	
	public final CommandResult execute(CommandContext context, Selection selection) {
		return execute(context, getParent(context, selection));
	}
	
	protected abstract boolean isEnabled(CommandContext context, SelectionItem parent);
	
	protected abstract CommandResult execute(CommandContext context, SelectionItem parent);

	
	private SelectionItem getParent(CommandContext context, Selection selection) {
		if (context.getScreen().getDao() instanceof TreeDao) {
			if (selection.size() == 1) {
				return selection.getSingleItem();
			}
			else if (selection.size() > 1) {
				return new ParentSelectionItem();
			}
		}
		return new ParentSelectionItem(context);
	}
	
	private class ParentSelectionItem implements SelectionItem {

		private String objectId;
		
		private Object object;
		
		private ParentSelectionItem() {
		}
		
		private ParentSelectionItem(CommandContext context) {
			objectId = context.getParentId();
			object = context.getParent();
		}

		public String getObjectId() {
			return objectId;
		}

		public Object getObject() {
			return object;
		}

		public int getRowIndex() {
			return -1;
		}
	}
	
}
