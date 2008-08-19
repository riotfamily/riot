package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.list.command.CommandContext;

public class ToggleHibernateStatisticsCommand 
		extends AbstractHibernateStatisticsCommand {
	
	public ToggleHibernateStatisticsCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	protected String getStyleClass(CommandContext context, String action) {
		return getSessionFactory().getStatistics().isStatisticsEnabled() ? 
				"switchOn" : "switchOff";
	}

	@Override
	public void doExecute(CommandContext context) {
		getSessionFactory().getStatistics().setStatisticsEnabled(!getSessionFactory().getStatistics().isStatisticsEnabled());
	}
}
