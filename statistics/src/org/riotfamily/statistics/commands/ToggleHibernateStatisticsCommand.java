package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.BatchResult;
import org.riotfamily.riot.list.command.result.RefreshListCommandsResult;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public class ToggleHibernateStatisticsCommand extends AbstractCommand {
	
	private SessionFactory sessionFactory;
	
	public ToggleHibernateStatisticsCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected String getStyleClass(CommandContext context, String action) {
		return sessionFactory.getStatistics().isStatisticsEnabled() ? 
				"switchOn" : "switchOff";
	}

	public CommandResult execute(CommandContext context) {
		sessionFactory.getStatistics().setStatisticsEnabled(
				!sessionFactory.getStatistics().isStatisticsEnabled());
		
		return new BatchResult(
				new RefreshSiblingsResult(context), 
				new RefreshListCommandsResult());
	}
}
