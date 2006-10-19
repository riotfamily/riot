package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ShowListResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.command.support.Clipboard;
import org.riotfamily.riot.list.ui.render.RenderContext;

public class PasteCommand extends AbstractCommand {
	
	public boolean isEnabled(RenderContext context) {
		Clipboard cb = Clipboard.get(context);
		return cb.canPaste(context);
	}
	
	public CommandResult execute(CommandContext context) {
		Clipboard cb = Clipboard.get(context);
		cb.paste(context);		
		return new ShowListResult(context);
	}

}
