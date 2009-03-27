package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.list.command.AbstractCommand;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.result.CommandResult;
import org.riotfamily.core.screen.list.command.result.RefreshSiblingsResult;

public class ClearHibernateStatisticsBaselineCommand extends AbstractCommand {

	private SessionFactory sessionFactory;
	
	@Override
	protected String getStyleClass(CommandContext context) {
		return "clear";
	}
	
	public ClearHibernateStatisticsBaselineCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public CommandResult execute(CommandContext context, Selection selection) {
		sessionFactory.getStatistics().clear();
		return new RefreshSiblingsResult();
	}
}
