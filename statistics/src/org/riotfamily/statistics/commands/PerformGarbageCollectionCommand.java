package org.riotfamily.statistics.commands;

import org.riotfamily.core.command.CommandContext;
import org.riotfamily.core.command.CommandResult;
import org.riotfamily.core.command.Selection;
import org.riotfamily.core.command.impl.AbstractCommand;
import org.riotfamily.core.command.result.RefreshSiblingsResult;

public class PerformGarbageCollectionCommand extends AbstractCommand  {
	
	public CommandResult execute(CommandContext context, Selection selection) {
		System.gc(); 
		return new RefreshSiblingsResult(context);
	}

}
