package org.riotfamily.statistics.commands;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class ClearRequestStatisticsBaselineCommand extends AbstractCommand  {

	private RequestCountFilterPlugin filterPlugin;
	
	public ClearRequestStatisticsBaselineCommand(RequestCountFilterPlugin filterPlugin) {
		this.filterPlugin = filterPlugin;
	}
	
	@Override
	protected String getIcon(String action) {
		return "clear";
	}

	public CommandResult execute(CommandContext context, Selection selection) {
		filterPlugin.reset();
		return new RefreshListResult();
	}

}
