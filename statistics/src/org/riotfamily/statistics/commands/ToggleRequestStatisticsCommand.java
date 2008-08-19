package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class ToggleRequestStatisticsCommand extends AbstractCommand  {

	private RequestCountFilterPlugin filterPlugin;
	
	public ToggleRequestStatisticsCommand(RequestCountFilterPlugin filterPlugin) {
		this.filterPlugin = filterPlugin;
	}

	@Override
	protected String getStyleClass(CommandContext context, String action) {
		return filterPlugin.isEnabled() ?
				"switchOn" : "switchOff";
	}
	
	public CommandResult execute(CommandContext context) {
		filterPlugin.setEnabled(!filterPlugin.isEnabled());
		return new RefreshSiblingsResult(context);
	}

}
