package org.riotfamily.core.screen.list.command.impl;

import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractSingleItemCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;

public class EditCommand extends AbstractSingleItemCommand<Object> {

	@Override
	protected String getIcon(String action) {
		return "pencil";
	}
	
	@Override
	protected CommandResult execute(CommandContext context, Object item) {
		ScreenContext childContext = context.createItemContext(item);
		return new GotoUrlResult(context.getRequest(), childContext.getUrl());
	}

}
