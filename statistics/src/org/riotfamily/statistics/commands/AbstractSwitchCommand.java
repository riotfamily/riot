package org.riotfamily.statistics.commands;

import org.riotfamily.core.command.CommandContext;
import org.riotfamily.core.command.CommandInfo;
import org.riotfamily.core.command.CommandResult;
import org.riotfamily.core.command.Selection;
import org.riotfamily.core.command.impl.AbstractCommand;
import org.riotfamily.core.command.result.BatchResult;
import org.riotfamily.core.command.result.RefreshListCommandsResult;
import org.riotfamily.core.command.result.RefreshSiblingsResult;

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
				new RefreshSiblingsResult(context), 
				new RefreshListCommandsResult());
	}
	
	protected abstract boolean isEnabled();
	
	protected abstract void setEnabled(boolean enabled);
}
