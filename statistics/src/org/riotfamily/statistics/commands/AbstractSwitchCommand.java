package org.riotfamily.statistics.commands;

import org.riotfamily.core.screen.list.command.AbstractCommand;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandInfo;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.CommandResult;
import org.riotfamily.core.screen.list.command.result.RefreshListCommandsResult;
import org.riotfamily.core.screen.list.command.result.RefreshSiblingsResult;

public abstract class AbstractSwitchCommand extends AbstractCommand {
	
	public  static final String ACTION_ENABLE = "enable";
	
	public static final String ACTION_DISABLE = "disable";
	
	@Override
	public CommandInfo getInfo(CommandContext context) {
		CommandInfo info = new CommandInfo();
		if (isEnabled()) {
			info.setLabel(ACTION_DISABLE);
			info.setStyleClass("switchOn");
		}
		else {
			info.setLabel(ACTION_ENABLE);
			info.setStyleClass("switchOff");
		}
		return info;
	}
		
	public CommandResult execute(CommandContext context, Selection selection) {
		setEnabled(!isEnabled());
		return new BatchResult(
				new RefreshSiblingsResult(), 
				new RefreshListCommandsResult());
	}
	
	protected abstract boolean isEnabled();
	
	protected abstract void setEnabled(boolean enabled);
}
