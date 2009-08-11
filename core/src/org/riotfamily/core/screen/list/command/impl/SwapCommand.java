package org.riotfamily.core.screen.list.command.impl;

import org.riotfamily.core.dao.Swapping;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public abstract class SwapCommand extends AbstractCommand {

	protected abstract int getSwapWith();

	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() == 1 && context.getScreen().getDao() instanceof Swapping) {
			Swapping dao = (Swapping) context.getScreen().getDao();
			return dao.canSwap(selection.getSingleItem().getObject(), 
					context.getParent(), context.getParams(), getSwapWith());
		}
		return false;
	}

	public CommandResult execute(CommandContext context, Selection selection) {
		Swapping dao = (Swapping) context.getScreen().getDao();
		dao.swapEntity(selection.getSingleItem().getObject(), context.getParent(), 
				context.getParams(), getSwapWith());
		
		return new RefreshListResult(selection.getSingleItem().getParentNodeId());
	}

}
