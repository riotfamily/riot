package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;

public class ToggleHibernateStatisticsCommand extends AbstractSwitchCommand {
	
	private SessionFactory sessionFactory;
	
	public ToggleHibernateStatisticsCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected boolean isEnabled() {
		return sessionFactory.getStatistics().isStatisticsEnabled();
	}
	
	@Override
	protected void setEnabled(boolean enabled) {
		sessionFactory.getStatistics().setStatisticsEnabled(enabled);
	}

}
