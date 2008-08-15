package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;

public class ToggleHibernateStatisticsCommand extends AbstractHibernateStatisticsCommand  {

	public void doExecute(CommandContext context) {
		getSessionFactory().getStatistics().setStatisticsEnabled(!getSessionFactory().getStatistics().isStatisticsEnabled());
	}
}
