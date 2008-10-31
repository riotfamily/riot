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
	public String getAction() {
		return isEnabled() ? ACTION_DISABLE : ACTION_ENABLE;
	}
	
	@Override
	public String getStyleClass() {
		return isEnabled() ? "switchOn" : "switchOff";
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
