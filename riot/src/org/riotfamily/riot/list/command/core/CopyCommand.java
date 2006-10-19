package org.riotfamily.riot.list.command.core;

import org.riotfamily.riot.dao.CopyAndPasteEnabledDao;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.SetRowStyleResult;
import org.riotfamily.riot.list.command.result.ShowListResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.command.support.Clipboard;
import org.riotfamily.riot.list.ui.render.RenderContext;

public class CopyCommand extends AbstractCommand {

	private static final String COPY_ROW_STYLE = "copied";
	
	public boolean isEnabled(RenderContext context) {
		if (context.getDao() instanceof CopyAndPasteEnabledDao) {
			Clipboard cb = Clipboard.get(context);
			if (cb.isCopied(context)) {
				context.addRowStyle(COPY_ROW_STYLE);
			}
			return true;	
		}
		return false;
	}
	
	public CommandResult execute(CommandContext context) {
		Clipboard cb = Clipboard.get(context);
		boolean empty = cb.isEmpty();
		cb.copy(context);
		if (empty) {
			return new SetRowStyleResult(context.getObjectId(), COPY_ROW_STYLE);
		}
		else {
			return new ShowListResult(context);
		}
	}
	
}
