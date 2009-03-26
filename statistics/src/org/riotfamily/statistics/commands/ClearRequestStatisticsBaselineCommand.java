package org.riotfamily.statistics.commands;

import org.riotfamily.core.command.CommandContext;
import org.riotfamily.core.command.CommandResult;
import org.riotfamily.core.command.Selection;
import org.riotfamily.core.command.impl.AbstractCommand;
import org.riotfamily.core.command.result.RefreshSiblingsResult;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class ClearRequestStatisticsBaselineCommand extends AbstractCommand  {

	private RequestCountFilterPlugin filterPlugin;
	
	public ClearRequestStatisticsBaselineCommand(RequestCountFilterPlugin filterPlugin) {
		this.filterPlugin = filterPlugin;
	}
	
	@Override
	protected String getStyleClass(CommandContext context) {
		return "clear";
	}

	public CommandResult execute(CommandContext context, Selection selection) {
		filterPlugin.reset();
		return new RefreshSiblingsResult(context);
	}

}
