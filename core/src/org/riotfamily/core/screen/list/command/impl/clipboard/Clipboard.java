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
	
	private void resetSelectionItems() {
		if (selection != null) {
			for (SelectionItem item : selection) {
				item.resetObject();
			}
		}
	}
	
	public boolean canPaste(CommandContext context, SelectionItem parent) {
		if (command != null) {
			resetSelectionItems();
			return command.canPaste(source, selection, context, parent);
		}
		return false;
	}
	
	public void paste(CommandContext context, SelectionItem parent, 
			NotificationResult notification) {
		
		resetSelectionItems();
		notification.setArgs(selection.size());
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
