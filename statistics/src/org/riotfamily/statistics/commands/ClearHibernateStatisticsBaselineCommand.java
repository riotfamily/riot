package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.list.command.CommandContext;

public class ClearHibernateStatisticsBaselineCommand 
		extends AbstractHibernateStatisticsCommand {

	public ClearHibernateStatisticsBaselineCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void doExecute(CommandContext context) {
		getSessionFactory().getStatistics().clear();
	}
}
