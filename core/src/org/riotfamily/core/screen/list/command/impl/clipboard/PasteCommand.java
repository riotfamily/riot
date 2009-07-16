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
package org.riotfamily.core.screen.list.command.impl.clipboard;

import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractChildCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;


public class PasteCommand extends AbstractChildCommand {

	@Override
	protected String getIcon(String action) {
		return "paste_plain";
	}
	
	@Override
	public boolean isEnabled(CommandContext context, SelectionItem parent) {
		Clipboard clipboard = Clipboard.get(context);
		return !clipboard.isEmpty() 
				&& isValidSource(clipboard.getSource(), context.getScreen()) 
				&& clipboard.canPaste(context, parent);
	}
	
	protected boolean isValidSource(ListScreen source, ListScreen target) {
		return source.equals(target);
	}
	
	public CommandResult execute(CommandContext context, SelectionItem parent) {
		NotificationResult notification = new NotificationResult(context, this)
				.setDefaultMessage("{0,choice,1#One item has|1<{0} items have} been pasted.");
		
		Clipboard.get(context).paste(context, parent, notification);
		return new BatchResult(
				notification,
				new RefreshListResult(parent.getObjectId()).refreshAll());
	}
	
}
