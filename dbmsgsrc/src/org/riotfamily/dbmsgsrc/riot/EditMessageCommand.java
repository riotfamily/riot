package org.riotfamily.dbmsgsrc.riot;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;

public abstract class EditMessageCommand extends AbstractCommand {

	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() != 1) {
			return false;
		}
		Message message = (Message) selection.getSingleItem().getObject();
		return !MessageBundleEntry.C_LOCALE.equals(message.getLocale());
	}
	
}
