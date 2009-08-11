package org.riotfamily.dbmsgsrc.riot;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.pages.model.Site;

public class TranslateMessageCommand extends AbstractCommand {
	
	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		Message message = (Message) selection.getSingleItem().getObject();
		return MessageBundleEntry.C_LOCALE.equals(message.getLocale());
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		Message message = (Message) selection.getSingleItem().getObject();
		Site site = (Site) context.getParent();
		message.getEntry().addTranslation(site.getLocale());
		return new RefreshListResult();
	}

}
