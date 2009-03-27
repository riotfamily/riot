package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.core.screen.list.command.AbstractCommand;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.result.CommandResult;
import org.riotfamily.core.screen.list.command.result.RefreshSiblingsResult;

public abstract class AbstractHibernateCacheCommand extends AbstractCommand {

	private SessionFactory sessionFactory;
	
	public AbstractHibernateCacheCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		doExecute(context, selection);
		
		return new RefreshSiblingsResult();
	}
	
	protected abstract void doExecute(CommandContext context, Selection selection);

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
