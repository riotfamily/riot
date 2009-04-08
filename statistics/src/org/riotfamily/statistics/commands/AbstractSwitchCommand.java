package org.riotfamily.statistics.commands;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.RefreshListCommandsResult;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public abstract class AbstractSwitchCommand extends AbstractCommand {
	
	public  static final String ACTION_ENABLE = "enable";
	
	public static final String ACTION_DISABLE = "disable";
	
	@Override
	protected String getAction(CommandContext context) {
		return isEnabled() ? ACTION_DISABLE : ACTION_ENABLE; 
	}
	
	@Override
	protected String getStyleClass(CommandContext context, String action) {
		return action == ACTION_DISABLE ? "switchOn" : "switchOff"; 
	}
		
	public CommandResult execute(CommandContext context, Selection selection) {
		setEnabled(!isEnabled());
		return new BatchResult(
				new RefreshListResult(), 
				new RefreshListCommandsResult());
	}
	
	protected abstract boolean isEnabled();
	
	protected abstract void setEnabled(boolean enabled);
}
