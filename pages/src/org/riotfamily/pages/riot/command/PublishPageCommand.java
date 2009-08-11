package org.riotfamily.pages.riot.command;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractBatchCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;
import org.riotfamily.pages.model.Page;

public class PublishPageCommand extends AbstractBatchCommand<Page> {

	@Override
	protected String getAction(CommandContext context) {
		return "publish";
	}
	
	@Override
	protected String getIcon(String action) {
		return "accept";
	}
	
	@Override
	protected boolean isEnabled(CommandContext context, Page page, int index,
			int selectionSize) {
		
		return !page.isPublished();
	}
	
	@Override
	protected CommandResult execute(CommandContext context, Page page,
			int index, int selectionSize) {

		page.publish();
		return new RefreshListResult();
	}
}
