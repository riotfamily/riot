package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class ClearRequestStatisticsBaselineCommand extends AbstractCommand  {

	private RequestCountFilterPlugin requestCountFilterPlugin;
	
	public RequestCountFilterPlugin getRequestCountFilterPlugin() {
		return requestCountFilterPlugin;
	}

	public void setRequestCountFilterPlugin(RequestCountFilterPlugin requestCountFilterPlugin) {
		this.requestCountFilterPlugin = requestCountFilterPlugin;
	}

	protected boolean isEnabled(CommandContext context, String action) {
		return true;
	}
	
	public CommandResult execute(CommandContext context) {

		requestCountFilterPlugin.reset();
		
		GotoUrlResult result = new GotoUrlResult(context, context.getListDefinition().getEditorUrl(null, context.getParentId(), context.getParentEditorId()));
		result.setTarget("top.frames.editor");
		return result;
	}


}
