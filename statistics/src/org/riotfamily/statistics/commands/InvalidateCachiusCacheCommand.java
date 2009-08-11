package org.riotfamily.statistics.commands;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;
import org.riotfamily.statistics.dao.CachiusStatisticsDao;

public class InvalidateCachiusCacheCommand extends AbstractCommand {
	
	@Override
	protected String getIcon(String action) {
		return "clear";
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		CachiusStatisticsDao dao = (CachiusStatisticsDao) context.getScreen().getDao();
		dao.getCachiusStatistics().invalidateAllItems();
		return new RefreshListResult();
	}

}
