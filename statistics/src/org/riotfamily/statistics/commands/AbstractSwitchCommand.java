package org.riotfamily.statistics.commands;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.BatchResult;
import org.riotfamily.riot.list.command.result.RefreshListCommandsResult;
import org.riotfamily.riot.list.command.result.RefreshSiblingsResult;

public abstract class AbstractSwitchCommand extends AbstractCommand {
	
	public  static final String ACTION_ENABLE = "enable";
	
	public static final String ACTION_DISABLE = "disable";
	
	@Override
	protected String getAction(CommandContext context) {
		return isEnabled() ? ACTION_DISABLE : ACTION_ENABLE;
	}
	
	@Override
	protected String getStyleClass(CommandContext context, String action) {
		return action.equals(ACTION_ENABLE) ? "switchOff" : "switchOn";
	}
	
	public CommandResult execute(CommandContext context) {
		setEnabled(!isEnabled());
		return new BatchResult(
				new RefreshSiblingsResult(context), 
				new RefreshListCommandsResult());
	}
	
	protected abstract boolean isEnabled();
	
	protected abstract void setEnabled(boolean enabled);
}
