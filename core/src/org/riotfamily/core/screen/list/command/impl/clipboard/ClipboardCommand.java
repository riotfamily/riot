package org.riotfamily.core.screen.list.command.impl.clipboard;


import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.result.NotificationResult;

public interface ClipboardCommand {

	public boolean canPaste(ListScreen source, Selection selection, 
			CommandContext context, SelectionItem parentItem);
	
	public void paste(ListScreen source, Selection selection, 
			CommandContext context, SelectionItem parentItem, 
			NotificationResult notification);
	
}
