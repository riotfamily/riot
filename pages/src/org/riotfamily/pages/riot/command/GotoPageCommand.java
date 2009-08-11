package org.riotfamily.pages.riot.command;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractBatchCommand;
import org.riotfamily.core.screen.list.command.result.PopupResult;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.view.PageFacade;

public class GotoPageCommand extends AbstractBatchCommand<Page> {
	
	@Override
	protected boolean isShowOnForm(CommandContext context) {
		return true;
	}
	
	@Override
	protected String getIcon(String action) {
		return "application_go";
	}

	@Override
	protected CommandResult execute(CommandContext context, Page page, 
			int index, int selectionSize) {
		
		String url = new PageFacade(page, context.getRequest()).getUrl();
		return new PopupResult(context.getRequest().getContextPath() + url);
	}

}
