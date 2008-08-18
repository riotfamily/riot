package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;

public class ToggleHibernateStatisticsCommand extends AbstractHibernateStatisticsCommand  {

	@Override
	protected String getStyleClass(CommandContext context, String action) {
		return getSessionFactory().getStatistics().isStatisticsEnabled() ? 
				"switchOff" : "switchOn";
	}

	@Override
	public void doExecute(CommandContext context) {
		getSessionFactory().getStatistics().setStatisticsEnabled(!getSessionFactory().getStatistics().isStatisticsEnabled());
	}
}
