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
