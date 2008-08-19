package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public class PerformGarbageCollectionCommand extends AbstractCommand  {

	public CommandResult execute(CommandContext context) {
		System.gc(); 
		return new RefreshSiblingsResult(context);
	}

}
