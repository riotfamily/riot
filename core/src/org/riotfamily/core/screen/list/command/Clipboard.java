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

import java.util.List;

import javax.servlet.http.HttpSession;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.ListScreen;

public class Clipboard {

	private static final String SESSION_ATTR = Clipboard.class.getName();

	private ClipboardCommand command;
	
	private ListScreen origin;
	
	private Selection selection;

	public void set(ListScreen origin, Selection selection, ClipboardCommand command) {
		this.origin = origin;
		this.selection = selection;
		this.command = command;
	}
	
	public boolean canPaste(ListScreen target, Object parent) {
		if (command != null) {
			return command.canPaste(origin, selection, target, parent);
		}
		return false;
	}
	
	public List<SelectionItem> paste(ListScreen target, Object parent) {
		List<SelectionItem> result = Generics.newArrayList();
		if (command != null) {
			command.paste(origin, selection, target, parent);
			for (SelectionItem item : selection) {
				result.add(item);
			}
			clear();
		}
		return result;
	}
	
	public void clear() {
		origin = null;
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
