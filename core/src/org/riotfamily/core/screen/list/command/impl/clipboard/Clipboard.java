/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	public boolean isEmpty() {
		return source == null; 
	}
	
	public ListScreen getSource() {
		return source;
	}
		
	public boolean canPaste(CommandContext context, SelectionItem parent) {
		if (command != null) {
			if (selection != null) {
				selection.resetObjects();
			}
			return command.canPaste(source, selection, context, parent);
		}
		return false;
	}
	
	public void paste(CommandContext context, SelectionItem parent, 
			NotificationResult notification) {
		
		if (selection != null) {
			selection.resetObjects();
			notification.setArgs(selection.size());
		}
		else {
			notification.setArgs(0);
		}
		
		command.paste(source, selection, context, parent, notification);
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
