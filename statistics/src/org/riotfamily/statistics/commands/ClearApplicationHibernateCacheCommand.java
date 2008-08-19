package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.list.command.CommandContext;

public class ClearApplicationHibernateCacheCommand 
		extends AbstractHibernateCacheCommand {

	public ClearApplicationHibernateCacheCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void doExecute(CommandContext context) {
		clearCache("org.riotfamily", true);
	}
}
