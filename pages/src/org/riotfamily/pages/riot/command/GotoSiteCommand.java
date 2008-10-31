package org.riotfamily.pages.riot.command;

import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.view.SiteFacade;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.core.PopupCommand;

public class GotoSiteCommand extends PopupCommand {

	public static final String STYLE_CLASS = "link";

	protected String getUrl(CommandContext context) {
		Site site = (Site) context.getBean();
		SiteFacade facade = new SiteFacade(site, context.getRequest());
		return facade.makeAbsolute("/");
	}
	
	@Override
	public String getStyleClass() {
		return STYLE_CLASS;
	}

}
