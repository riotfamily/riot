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
