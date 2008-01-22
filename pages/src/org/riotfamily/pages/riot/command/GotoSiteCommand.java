package org.riotfamily.pages.riot.command;

import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.core.PopupCommand;

public class GotoSiteCommand extends PopupCommand {

	public static final String STYLE_CLASS = "link";

	protected String getUrl(CommandContext context) {
		Site site = (Site) context.getBean();
		return site.getAbsoluteUrl(context.getRequest()).append("/").toString();
	}
	
	protected String getStyleClass(CommandContext context, String action) {
		return STYLE_CLASS;
	}

}
