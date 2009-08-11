package org.riotfamily.dbmsgsrc.riot;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public class RemoveEmptyEntriesCommand extends AbstractCommand {

	@Override
	protected String getIcon(String action) {
		return "delete";
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		//String bundle = ((MessageBundleEntryDao) context.getDao()).getBundle();
		//dao.removeEmptyEntries(bundle);
		return new RefreshListResult();
	}
}
