package org.riotfamily.core.screen.list.command.impl;

import org.riotfamily.core.screen.list.command.CommandContext;

public class MoveUpCommand extends SwapCommand {

	@Override
	protected int getSwapWith() {
		return -1;
	}
	
	@Override
	protected String getAction(CommandContext context) {
		return "moveUp";
	}
	
	@Override
	protected String getIcon(String action) {
		return "arrow_up";
	}

}
