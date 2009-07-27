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
package org.riotfamily.core.screen.list.command.impl.support;

import org.riotfamily.core.dao.Tree;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;

public abstract class AbstractChildCommand extends AbstractCommand {

	@Override
	public final boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() > 1) {
			return false;
		}
		return isEnabled(context, getParent(context, selection));
	}
	
	public final CommandResult execute(CommandContext context, Selection selection) {
		return execute(context, getParent(context, selection));
	}
	
	protected boolean isEnabled(CommandContext context, SelectionItem parent) {
		return true;
	}
	
	protected abstract CommandResult execute(CommandContext context, SelectionItem parent);

	
	private SelectionItem getParent(CommandContext context, Selection selection) {
		if (context.getScreen().getDao() instanceof Tree) {
			if (selection.size() == 1) {
				return selection.getSingleItem();
			}
		}
		return new RootSelectionItem();
	}
	
	private static class RootSelectionItem implements SelectionItem {

		public String getObjectId() {
			return null;
		}

		public Object getObject() {
			return null;
		}
		
		public void resetObject() {
		}

		public String getParentNodeId() {
			return null;
		}
	}
	
}
