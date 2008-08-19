package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class ClearRequestStatisticsBaselineCommand extends AbstractCommand  {

	private RequestCountFilterPlugin filterPlugin;
	
	public ClearRequestStatisticsBaselineCommand(RequestCountFilterPlugin filterPlugin) {
		this.filterPlugin = filterPlugin;
	}

	public CommandResult execute(CommandContext context) {
		filterPlugin.reset();
		return new RefreshSiblingsResult(context);
	}

}
