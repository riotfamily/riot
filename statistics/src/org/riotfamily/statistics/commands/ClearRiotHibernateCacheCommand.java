package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;

public class ClearRiotHibernateCacheCommand extends AbstractHibernateCacheCommand{

	public void doExecute(CommandContext context) {
		clearCache("org.riotfamily", false); 
	}
}
