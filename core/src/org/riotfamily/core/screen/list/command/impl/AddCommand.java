package org.riotfamily.core.screen.list.command.impl;

import org.riotfamily.core.dao.Constraints;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractChildCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;

public class AddCommand extends AbstractChildCommand {
	
	@Override
	protected boolean isEnabled(CommandContext context, SelectionItem parent) {
		RiotDao dao = context.getScreen().getDao();
		if (dao instanceof Constraints) {
			Constraints cd = (Constraints) dao;
			return cd.canAdd(parent.getObject());
		}
		return true;
	}
	
	@Override
	protected CommandResult execute(CommandContext context, SelectionItem parent) {
		ScreenContext childContext = context.createNewItemContext(parent.getObject());
		return new GotoUrlResult(context.getRequest(), childContext.getUrl());
	}

}
