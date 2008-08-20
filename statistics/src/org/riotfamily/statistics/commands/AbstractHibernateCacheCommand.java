package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public abstract class AbstractHibernateCacheCommand extends AbstractCommand {

	private SessionFactory sessionFactory;
	
	public AbstractHibernateCacheCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public CommandResult execute(CommandContext context) {
		doExecute(context);
		return new RefreshSiblingsResult(context);
	}
	
	protected abstract void doExecute(CommandContext context);

	protected void clearCache(String entityPrefix, boolean inverse) {
		String[] entityNames = sessionFactory.getStatistics().getEntityNames();
		for (String name : entityNames) {
			boolean match = name.startsWith(entityPrefix);
			if (match ^ inverse) {
				evictCacheEntry(name, false);
			}
		}
	}
	
	protected void evictCacheEntry(String entity, boolean collection) {
		if (collection) {
			sessionFactory.evictCollection(entity);
		} 
		else {
			Class<?> clazz = SpringUtils.classForName(entity);
			sessionFactory.evict(clazz);
		}
	}
}
