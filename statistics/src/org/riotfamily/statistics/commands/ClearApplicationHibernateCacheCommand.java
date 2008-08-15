package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;

public class ClearApplicationHibernateCacheCommand extends AbstractHibernateCacheCommand{

	public void doExecute(CommandContext context) {
		clearCache("org.riotfamily", true);
	}
}
