package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;

public class ClearApplicationHibernateCacheCommand 
		extends AbstractHibernateCacheCommand {

	public ClearApplicationHibernateCacheCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	protected String getStyleClass(CommandContext context) {
		return "clear";
	}

	public void doExecute(CommandContext context, Selection selection) {
		clearCache("org.riotfamily", true);
	}
	
}
