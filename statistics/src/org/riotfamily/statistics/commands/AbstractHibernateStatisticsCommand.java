package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public abstract class AbstractHibernateStatisticsCommand extends AbstractCommand  {

	private SessionFactory sessionFactory;

	public AbstractHibernateStatisticsCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public CommandResult execute(CommandContext context) {
		doExecute(context);
		return new RefreshSiblingsResult(context);
	}

	protected abstract void doExecute(CommandContext context) ;
}
