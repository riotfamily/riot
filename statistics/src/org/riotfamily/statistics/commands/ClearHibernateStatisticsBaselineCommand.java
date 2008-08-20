package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public class ClearHibernateStatisticsBaselineCommand extends AbstractCommand {

	private SessionFactory sessionFactory;
	
	public ClearHibernateStatisticsBaselineCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public CommandResult execute(CommandContext context) {
		sessionFactory.getStatistics().clear();
		return new RefreshSiblingsResult(context);
	}
}
