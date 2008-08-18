package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;

public class ClearHibernateStatisticsBaselineCommand extends AbstractHibernateStatisticsCommand  {

	public void doExecute(CommandContext context) {
		getSessionFactory().getStatistics().clear();
	}
}
