package org.riotfamily.core.screen.list.command;


public interface Command {

	public CommandInfo getInfo(CommandContext context);
	
	public boolean isEnabled(CommandContext context, Selection selection);
	
	public CommandResult execute(CommandContext context, Selection selection);

}
