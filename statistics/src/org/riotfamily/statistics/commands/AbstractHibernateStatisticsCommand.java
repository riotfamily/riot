package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.GotoUrlResult;

public abstract class AbstractHibernateStatisticsCommand extends AbstractCommand  {

	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected boolean isEnabled(CommandContext context, String action) {
		return true;
	}
	
	public CommandResult execute(CommandContext context) {
		doExecute(context);
		GotoUrlResult result = new GotoUrlResult(context, context.getListDefinition().getEditorUrl(null, context.getParentId(), context.getParentEditorId()));
		result.setTarget("top.frames.editor");
		return result;
	}

	protected abstract void doExecute(CommandContext context) ;
}
