package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.list.command.CommandContext;

public class ClearRiotHibernateCacheCommand 
		extends AbstractHibernateCacheCommand {

	public ClearRiotHibernateCacheCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void doExecute(CommandContext context) {
		clearCache("org.riotfamily", false); 
	}
}
