package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public class ClearHibernateStatisticsBaselineCommand extends AbstractCommand {

	private SessionFactory sessionFactory;
	
	@Override
	protected String getIcon(String action) {
		return "clear";
	}
	
	public ClearHibernateStatisticsBaselineCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public CommandResult execute(CommandContext context, Selection selection) {
		sessionFactory.getStatistics().clear();
		return new RefreshListResult();
	}
}
