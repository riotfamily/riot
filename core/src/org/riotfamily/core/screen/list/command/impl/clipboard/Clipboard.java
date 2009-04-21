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

import javax.servlet.http.HttpSession;

import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.result.NotificationResult;

public class Clipboard {

	private static final String SESSION_ATTR = Clipboard.class.getName();

	private ClipboardCommand command;
	
	private ListScreen source;
	
	private Selection selection;

	public void set(ListScreen source, Selection selection, ClipboardCommand command) {
		this.source = source;
		this.selection = selection;
		this.command = command;
	}
	
	public boolean isAlreadySet(ClipboardCommand command, Selection selection) {
		return command.equals(this.command) && selection.equals(this.selection); 
	}
	
	public boolean canPaste(ListScreen target, SelectionItem parent) {
		if (command != null) {
			return command.canPaste(source, selection, target, parent);
		}
		return false;
	}
	
	public void paste(ListScreen target, SelectionItem parent, 
			NotificationResult notification) {
		
		notification.setArgs(selection.size());
		command.paste(source, selection, target, parent, notification);
		clear();
	}
	
	public void clear() {
		source = null;
		selection = null;
		command = null;
	}
	
	public static Clipboard get(CommandContext context) {
		HttpSession session = context.getRequest().getSession();
		Clipboard clipboard = (Clipboard) session.getAttribute(SESSION_ATTR);
		if (clipboard == null) {
			clipboard = new Clipboard();
			session.setAttribute(SESSION_ATTR, clipboard);
		}
		return clipboard;
	}

}
