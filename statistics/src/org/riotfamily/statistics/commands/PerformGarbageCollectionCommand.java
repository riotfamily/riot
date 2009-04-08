package org.riotfamily.statistics.commands;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public class PerformGarbageCollectionCommand extends AbstractCommand  {
	
	public CommandResult execute(CommandContext context, Selection selection) {
		System.gc(); 
		return new RefreshListResult();
	}

}
